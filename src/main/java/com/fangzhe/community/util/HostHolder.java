package com.fangzhe.community.util;

import com.fangzhe.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author fang
 * 持有用户信息，用于代替session对象
 */
@Component
public class HostHolder {
    /**
     *     以线程为key存入map
     */
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clean(){
        users.remove();
    }
}
