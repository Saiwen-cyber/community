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
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);

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
}
