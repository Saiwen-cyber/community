package com.fangzhe.community.dao;

import com.fangzhe.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author fang
 */
@Mapper
@Component
public interface CommentMapper {
    /**
     * 根据实体类型分页查询,本条博文的第一层评论
     * @param entityType
     * @return
     */
    List<Comment> selectCommentByEntity(int entityType, int entityId, int offSet, int limit );
    /**
     * 根据实体类型查询总数
     * @param entityType
     * @return
     */
    int selectCountByEntity(int entityType, int entityId);
    /**
     * 根据实体类型查询总数
     * @param comment
     * @return
     */
    int insertComment(Comment comment);
    /**
     * 根据id查1个comment
     * @param id
     * @return
     */
    Comment selectCommentById(int id);
}

