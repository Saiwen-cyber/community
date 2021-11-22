package com.fangzhe.community.service;

import com.fangzhe.community.dao.DiscussPostMapper;
import com.fangzhe.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fang
 */
@Service
public class DiscussPosService {

    @Autowired
    DiscussPostMapper discussPostMapper;

    public List<DiscussPost> findDiscussPosts(int userId,int offset, int limit){
        return  discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }
    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

}
