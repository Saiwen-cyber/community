package com.fangzhe.community.controller;

import com.fangzhe.community.entity.Comment;
import com.fangzhe.community.entity.DiscussPost;
import com.fangzhe.community.entity.Event;
import com.fangzhe.community.entity.User;
import com.fangzhe.community.event.EventProducer;
import com.fangzhe.community.service.CommentService;
import com.fangzhe.community.service.DiscussPosService;
import com.fangzhe.community.util.CommunityConstant;
import com.fangzhe.community.util.HostHolder;
import com.fangzhe.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

/**
 * @author fang
 */
@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @Autowired
    DiscussPosService discussPosService;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    RedisTemplate redisTemplate;
    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        User user = hostHolder.getUser();
        comment.setUserId(user.getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);
        /**
         * 一条评论发出时，无论如何 首先触发 TOPIC_COMMENT 评论事件
         * 如果评论的对象是post，那么 需要同步 elastic search搜索引擎 与mysql 数据库中的帖子。
         */

        //触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(comment.getUserId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);
        if(comment.getEntityType() == ENTITY_TYPE_POST){
          DiscussPost target = discussPosService.findDiscussPostById(discussPostId);
          event.setEntityUserId(target.getUserId());
        } else if(comment.getEntityType() == ENTITY_TYPE_COMMENT){
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            //触发发帖事件
                event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(user.getId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
            eventProducer.fireEvent(event);
            //待更新帖子分数
            String redisKey = RedisKeyUtil.getPostScore();
            redisTemplate.opsForSet().add(redisKey, discussPostId);
        }

        return "redirect:/discuss/detail/"+discussPostId;
    }
}
