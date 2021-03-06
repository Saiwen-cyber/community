package com.fangzhe.community.controller;

import com.fangzhe.community.entity.*;
import com.fangzhe.community.entity.Event;
import com.fangzhe.community.event.EventProducer;
import com.fangzhe.community.service.CommentService;
import com.fangzhe.community.service.DiscussPosService;
import com.fangzhe.community.service.LikeService;
import com.fangzhe.community.service.UserService;
import com.fangzhe.community.util.CommunityConstant;
import com.fangzhe.community.util.CommunityUtil;
import com.fangzhe.community.util.HostHolder;
import com.fangzhe.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author fang
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant{

    @Autowired
    DiscussPosService discussPosService;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    @Autowired
    LikeService likeService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    RedisTemplate redisTemplate;

    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content){
        User user = hostHolder.getUser();
        if(user == null){
            return CommunityUtil.getJSONString(403,"您还没有登录哦！");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPost.setType(POST_TYPE_NORMAL);
        discussPost.setCommentCount(0);
        discussPost.setScore(0.0);

        //正常帖
        discussPost.setStatus(POST_STATUS_NORMAL);
        discussPosService.addDiscussPost(discussPost);

        //触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(discussPost.getId());
        eventProducer.fireEvent(event);
        //待更新帖子分数
        String redisKey = RedisKeyUtil.getPostScore();
        redisTemplate.opsForSet().add(redisKey, discussPost.getId());

        //报错统一处理
        return CommunityUtil.getJSONString(0,"发布成功");
    }

    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        //查询帖子
        DiscussPost post = discussPosService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);
        //作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

        //点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeCount", likeCount);
        //当前登录用户 点赞状态
        int likStatus = hostHolder.getUser() != null ?
                    likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, discussPostId) : 0;
        model.addAttribute("likeStatus", likStatus);

        //评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount()== null ?0:post.getCommentCount());

        //评论：给帖子的评论
        //回复：给评论的评论
        //评论列表
        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        //评论Vo列表
        List<Map<String,Object>> commentVoList = new ArrayList<>();
        if(commentList!=null){
            for (Comment comment: commentList) {
                //评论Vo
                Map<String,Object> commentVo = new HashMap<>();
                //添加评论
                commentVo.put("comment",comment);
                //作者
                commentVo.put("user", userService.findUserById(comment.getUserId()));
                //点赞数量
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);
                //当前登录用户 点赞状态
                likStatus = hostHolder.getUser() != null ?
                        likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId()) : 0;
                commentVo.put("likeStatus", likStatus);

                //回复列表
                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId(),0, Integer.MAX_VALUE);
                //回复Vo列表
                List<Map<String,Object>> replyVoList = new ArrayList<>();
                if(replyList != null){
                    for (Comment reply: replyList) {
                        Map<String,Object> replyVo = new HashMap<>();
                        //回复
                        replyVo.put("reply",reply);
                        //作者
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        //回复的目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);
                        //点赞数量
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount);
                        //当前登录用户 点赞状态
                        likStatus = hostHolder.getUser() != null ?
                                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId()) : 0;
                        replyVo.put("likeStatus", likStatus);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys",replyVoList);

                //回复数量
                int replyCount = commentService.findCommentsCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("replyCount",replyCount);
                commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments",commentVoList);
        return "site/discuss-detail";
    }

    @PostMapping("/top")
    @ResponseBody
    public String setTop(int id){
        //elasticserach 更新搜索引擎
        discussPosService.updateType(id,POST_TYPE_TOP);
        DiscussPost discussPost = discussPosService.findDiscussPostById(id);
        //触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }

    @PostMapping("/essence")
    @ResponseBody
    public String setEssence(int id){

        discussPosService.updateStatus(id,POST_STATUS_ESSENCE);
        //触发发帖事件
        //elastic search 更新搜索引擎
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);
        //待更新帖子分数
        String redisKey = RedisKeyUtil.getPostScore();
        redisTemplate.opsForSet().add(redisKey, id);

        return CommunityUtil.getJSONString(0);
    }

    @PostMapping("/block")
    @ResponseBody
    public String setBlock(int id){
        discussPosService.updateStatus(id,POST_STATUS_BLOCK);
        //触发拉黑帖子事件
        //elastic search 更新搜索引擎
        Event event = new Event()
                .setTopic(TOPIC_BLOCK)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }

}
