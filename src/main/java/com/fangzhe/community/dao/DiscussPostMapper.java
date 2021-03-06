package com.fangzhe.community.dao;

import com.fangzhe.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author fang
 */
@Mapper
@Component
public interface DiscussPostMapper {
    /**
     * (根据id)分页查询帖子
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode);

    /**
     * 查询行数
     * @param userId
     * @return
     */
    int selectDiscussPostRows(@Param("userId") int userId);

    /**
     * 新增帖子
     * @param discussPost
     * @return
     */
    int insertDiscussPost(DiscussPost discussPost);
    /**
     * 查询一个帖子详情
     * @param id
     * @return
     */
    DiscussPost selectDiscussPostByid(int id);
    /**
     * 更新帖子
     * @param id commentCount
     * @return
     */
    int updateCommentCount(int id, int commentCount);


    /**
     * 点击置顶、修改帖子类型
     * @param id
     * @param type
     * @return
     */
    int updateType(int id, int type);

    /**
     * 点击“加精”、“删除(拉黑)”，修改帖子的状态。
     * @param id
     * @param status
     * @return
     */
    int updateStatus(int id, int status);

    int deleteDiscussPost(int id);

    int updateScore(int id, double score);

}
