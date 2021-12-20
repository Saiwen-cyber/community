package com.fangzhe.community.dao;

import com.fangzhe.community.entity.LoginTicket;
import com.fangzhe.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author fang
 */
@Mapper
@Component
public interface MessageMapper {

    /**
     * 查询当前用户的会话列表，针对每个列表只返回 一条最新的数据
     */
    List<Message> selectConversations(int userId,int offSet, int limit);

    /**
     * 查询当前用户的会话数量
     * @param userId
     * @return
     */
    int selectConversationCount(int userId);

    /**
     * 查询某个会话所包含的私信列表
     * @param conversationId
     * @param offSet
     * @param limit
     * @return
     */
    List<Message> selectLetters(String conversationId,int offSet, int limit);

    /**
     * 查询某个会话所包含的私信数量
     * @param conversationId
     * @return
     */
    int selectLetterCount(String conversationId);

    /**
     * 查询某个会话未读私信数量
     * @param conversationId
     * @return
     */
    int selectLetterUnreadCount(int userId,String conversationId);//动态conversationId

    /**
     * 新增一个消息
     * @param message
     * @return
     */
    int insertMessage(Message message);

    /**
     * 修改消息状态、（已读、删除
     * @param ids
     * @param status
     * @return
     */
    int updateStatus(List<Integer> ids, int status);

    /**
     * 查询某一主题下的最新通知
     * @param userId
     * @param topic
     * @return
     */
    Message selectLatestNotice(int userId, String topic);
    /**
     * 查询某一主题下的未读通知数量 topic 为 null 时  查询结果：全部未读系统通知
     * @param userId
     * @param topic
     * @return
     */
    int selectNoticeUnreadCount(int userId, String topic);
    /**
     * 查询某一主题下的通知数量
     * @param userId
     * @param topic
     * @return
     */
    int selectNoticeCount(int userId, String topic);



}
