package com.fangzhe.community.service;

import com.fangzhe.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * @author fang
 */
@Service
public class FollowService {
    @Autowired
    private RedisTemplate redisTemplate;

    //关注  人关注实体，实体被人关注
    public void follow(int userId, int entityType, int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                //一个user的关注人数
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
                //一个实体的粉丝人数
                String followerKey = RedisKeyUtil.getFollowerKey(entityId,entityType);

                operations.multi();
                operations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
                operations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());

                operations.exec();
                return null;
            }
        });
    }

    /**
     *  取消关注  人关注实体，实体被人关注
     */
    public void unfollow(int userId, int entityType, int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                //一个user的关注人数
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
                //一个实体的粉丝人数
                String followerKey = RedisKeyUtil.getFollowerKey(entityId,entityType);

                operations.multi();
                operations.opsForZSet().remove(followeeKey,entityId);
                operations.opsForZSet().remove(followerKey,userId);

                operations.exec();
                return null;
            }
        });
    }
    /**
     * 查询关注实体数量
     */
    public long findFolloweeCount(int userId, int entityType){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }
    /**
     * 查询实体粉丝数量
     */
    public long findFollowerCount(int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }
    /**
     * 查询当前用户是否关注 某实体
     */
    public boolean hasFollowed(int userId ,int entityType, int entityId){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }

}
