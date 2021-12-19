package com.fangzhe.community.dao;

import com.fangzhe.community.entity.LoginTicket;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @author fang
 */
@Mapper
@Deprecated
public interface LoginTicketMapper {

    int insertLoginTicket(LoginTicket loginTicket);

    int updateLoginTicket(String ticket,int status);

    LoginTicket selectLoginTicket(String ticket);
}
