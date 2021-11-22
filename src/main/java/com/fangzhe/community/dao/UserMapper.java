package com.fangzhe.community.dao;

import com.fangzhe.community.entity.DiscussPost;
import com.fangzhe.community.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author fang
 */
@Mapper
public interface UserMapper {

    /**
     * (根据id)查询user
     * @param id
     * @return User
     */
    User findUserById(int id);
    /**
     * (根据username)查询user
     * @param username
     * @return User
     */
    User findUserByUsername(String username);
    /**
     * (根据email)查询user
     * @param email
     * @return User
     */
    User findUserByEmail(String email);
    /**
     * 插入user
     * @param user
     */
    int insertUser(User user);
    /**
     * 插入user
     * @param userId,status
     */
    int updateStatus(Integer userId, Integer status);

}
