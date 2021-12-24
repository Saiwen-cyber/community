package com.fangzhe.community.service;

import com.fangzhe.community.dao.CommentMapper;
import com.fangzhe.community.dao.MessageMapper;
import com.fangzhe.community.entity.Comment;
import com.fangzhe.community.entity.Message;
import com.fangzhe.community.util.CommunityConstant;
import com.fangzhe.community.util.HostHolder;
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


    public int findLetterUnreadCount(int userId, String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId,conversationId);
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

    /**
     * 找到主题下最新的一条通知记录
     * @param userId
     * @param topic
     * @return
     */
    public Message findLatestNotice(int userId, String topic){
        return messageMapper.selectLatestNotice(userId, topic);
    }

    /**
     * 找到该主题下的通知数量
     * @param userId
     * @param topic
     * @return
     */
    public int findNoticeCount(int userId, String topic){
        return messageMapper.selectNoticeCount(userId, topic);
    }

    /**
     * 找到该主题下未读消息数量
     * @param userId
     * @param topic
     * @return
     */
    public int findNoticeUnreadCount(int userId, String topic){
        return messageMapper.selectNoticeUnreadCount(userId, topic);
    }
    public List<Message> findNotices(int userId, String topic, int offset, int limit){
        return messageMapper.selectNotices(userId,topic,offset,limit);
    }

}
