package com.fangzhe.community.service;

import com.fangzhe.community.dao.DiscussPostMapper;
import com.fangzhe.community.entity.DiscussPost;
import com.fangzhe.community.util.CommunityUtil;
import com.fangzhe.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author fang
 */
@Service
public class DiscussPosService {

    @Autowired
    DiscussPostMapper discussPostMapper;
    @Autowired
    SensitiveFilter sensitiveFilter;


    public List<DiscussPost> findDiscussPosts(int userId,int offset, int limit){
        return  discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }
    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    public int addDiscussPost(DiscussPost discussPost){
        if (discussPost == null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        //转义HTML标记
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        //过滤敏感词
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        return discussPostMapper.insertDiscussPost(discussPost);
    }

    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostByid(id);
    }
}
