package com.fangzhe.community.controller;

import com.fangzhe.community.entity.Event;
import com.fangzhe.community.entity.User;
import com.fangzhe.community.event.EventProducer;
import com.fangzhe.community.service.LikeService;
import com.fangzhe.community.util.CommunityConstant;
import com.fangzhe.community.util.CommunityUtil;
import com.fangzhe.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fang
 */
@Controller
public class LikeController implements CommunityConstant {
    @Autowired
    LikeService likeService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;
    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId,int postId){
        User user = hostHolder.getUser();
        if(user == null){
            throw new NullPointerException("请您登录后点赞！");
        }

        //点赞
        likeService.like(user.getId(),entityType,entityId,entityUserId);
        //点赞数量
        Long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        //状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        //返回结果
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);
        //触发点赞事件
        if(likeStatus == 1){
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(user.getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId",postId);
            eventProducer.fireEvent(event);
        }

        return CommunityUtil.getJSONString(0,null, map);
    }
}
