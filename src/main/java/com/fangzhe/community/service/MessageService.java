package com.fangzhe.community.service;

import com.fangzhe.community.dao.CommentMapper;
import com.fangzhe.community.dao.MessageMapper;
import com.fangzhe.community.entity.Comment;
import com.fangzhe.community.entity.Message;
import com.fangzhe.community.util.CommunityConstant;
import com.fangzhe.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author fang
 */
@Service
public class MessageService implements CommunityConstant{

    @Autowired
    MessageMapper messageMapper;

    @Autowired
    SensitiveFilter sensitiveFilter;
    public List<Message> findConversations(int userId, int offSet, int limit) {
        return  messageMapper.selectConversations(userId,offSet,limit);
    }


    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }


    public List<Message> findLetters(String conversationId, int offSet, int limit) {
        return messageMapper.selectLetters(conversationId,offSet,limit);
    }


    public int findLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }


    public int findLetterUnread(int userId, String conversationId) {
        return messageMapper.selectLetterUnread(userId,conversationId);
    }

    public int addMessage(Message message){
       message.setContent(HtmlUtils.htmlEscape(message.getContent()));
       message.setContent(sensitiveFilter.filter(message.getContent()));
       return messageMapper.insertMessage(message);
    }

    /**
     * 修改消息状态为已读
     * @param ids
     * @return
     */
    public int readMessage(List<Integer> ids){
        return messageMapper.updateStatus(ids,1);
    }
}
