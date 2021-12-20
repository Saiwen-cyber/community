package com.fangzhe.community.service;

import com.fangzhe.community.dao.CommentMapper;
import com.fangzhe.community.dao.LoginTicketMapper;
import com.fangzhe.community.dao.UserMapper;
import com.fangzhe.community.entity.Comment;
import com.fangzhe.community.entity.LoginTicket;
import com.fangzhe.community.entity.User;
import com.fangzhe.community.util.CommunityConstant;
import com.fangzhe.community.util.CommunityUtil;
import com.fangzhe.community.util.MailClient;
import com.fangzhe.community.util.SensitiveFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;

/**
 * @author fang
 */
@Service
public class CommentService implements CommunityConstant{
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private DiscussPosService discussPosService;

    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offSet, int limit ){
        return commentMapper.selectCommentByEntity(entityType, entityId, offSet,limit);
    }

    public int findCommentsCount(int entityType, int entityId){
        return commentMapper.selectCountByEntity(entityType, entityId);
    }
    /**
      *隔离级别 以及传播机制
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,rollbackFor = {})
    public int addComment(Comment comment){

        if(comment == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //标签、敏感词过滤
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        //添加评论
        int rows = commentMapper.insertComment(comment);
        //更新帖子评论对象的评论数量
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            int count = commentMapper.selectCountByEntity(comment.getEntityType(),comment.getEntityId());
            discussPosService.updateCommentCount(comment.getEntityId(),count);
        }
        return rows;
    }

    public Comment findCommentById(int id){
        return commentMapper.selectCommentById(id);
    }
}
