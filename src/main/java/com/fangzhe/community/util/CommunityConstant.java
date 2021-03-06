package com.fangzhe.community.util;

/**
 * @author fang
 */
public interface CommunityConstant {
    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;
    /**
     * 激活成功
     */
    int ACTIVATION_REPEAT = 1;
    /**
     * 激活成功
     */
    int ACTIVATION_FAIL =2;
    /**
     * 默认登录状态的登陆凭证超时时间
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    /**
     * 记住我登录状态的登录凭证超时时间
     */
    int REMEMBERME_EXPIRED_SECONDS = 3600 * 24 * 100;
    /**
     * 实体类型:帖子
     */
    int ENTITY_TYPE_POST = 1;
    /**
     * 实体类型:评论
     */
    int ENTITY_TYPE_COMMENT = 2;
    /**
     * 实体类型:用户
     */
    int ENTITY_TYPE_USER = 3;
    /**
     * 主题： 点赞
     */
    String TOPIC_LIKE = "like";
    /**
     * 主题： 评论
     */
    String TOPIC_COMMENT = "comment";
    /**
     * 主题： 关注
     */
    String TOPIC_FOLLOW = "follow";
    /**
     * 主题： 发帖
     */
    String TOPIC_PUBLISH = "publish";
    /**
     * 主题： 拉黑
     */
    String TOPIC_BLOCK = "block";
    /**
     * 主题： 分享（image）
     */
    String TOPIC_SHARE = "share";
    /**
     * 系统用户Id
     */
    int SYSTEM_USER_ID = 1;
    /**
     * 普通帖
     */
    int POST_TYPE_NORMAL = 0;
    /**
     * 置顶帖
     */
    int POST_TYPE_TOP = 1;
    /**
     * 普通帖
     */
    int POST_STATUS_NORMAL = 0;
    /**
     * 精华帖
     */
    int POST_STATUS_ESSENCE  = 1;
    /**
     * 拉黑
     */
    int POST_STATUS_BLOCK  = 2;
    /**
     * 权限：普通用户
     */
    String AUTHORITY_USER = "user";
    /**
     * 权限：管理员
     */
    String AUTHORITY_ADMIN = "admin";
    /**
     * 权限：版主
     */
    String AUTHORITY_MODERATOR = "moderator";

}
