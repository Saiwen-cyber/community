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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;

/**
 * @author fang
 */
@Service
public class CommentService{
    @Autowired
    private CommentMapper commentMapper;

    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offSet, int limit ){
        return commentMapper.selectCommentByEntity(entityType, entityId, offSet,limit);
    }

    public int findCommentsCount(int entityType, int entityId){
        return commentMapper.selectCountByEntity(entityType, entityId);
    }
}
