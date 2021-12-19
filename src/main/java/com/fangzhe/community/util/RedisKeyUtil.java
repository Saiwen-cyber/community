package com.fangzhe.community.util;

/**
 * @author fang
 */
public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";
    /**
     *     某个实体的赞 key
     *      like:entity:entityType:entityId -> set(userId)(集合)
     *      满足需求变化 不只是数字、  （点赞人）。
     */
    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 某个用户的赞 key
     * @param userId
     * like:user:userId
     * @return
     */
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }
    /**
     * 某个用户关注的实体
     * followee:userId:entityType -> zset(entityId,now)
     */
    public static String getFolloweeKey(int userId, int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT +entityType;
    }

    /**
     * 某个实体有的粉丝
     * followee:entityType:entityId -> zset(entityId,now)
     */
    public static String getFollowerKey(int entityType, int entityId){
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT +entityId;
    }

    /**
     * 登陆验证码
     */
    public static String getKaptcha(String owner){
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    /**
     * 登陆的凭证
     */
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET + SPLIT + ticket;
    }
    /**
     * redis 缓存用户登录信息 的key
     */
    public static String getUserKey(int userId){
        return PREFIX_USER + SPLIT + userId;
    }

}
