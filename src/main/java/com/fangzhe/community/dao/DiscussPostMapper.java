package com.fangzhe.community.dao;

import com.fangzhe.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author fang
 */
@Mapper
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
}
