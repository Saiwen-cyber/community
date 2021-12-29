package com.fangzhe.community.controller.interceptor;

import com.fangzhe.community.entity.Message;
import com.fangzhe.community.service.MessageService;
import com.fangzhe.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author fang
 */
@Component
public class MessageInterceptor implements HandlerInterceptor {

    @Autowired
    MessageService messageService;
    @Autowired
    HostHolder hostHolder;
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if(hostHolder.getUser() != null && modelAndView != null){
            int noticeUnreadCount =  messageService.findNoticeUnreadCount(hostHolder.getUser().getId(),null);
            int letterUnreadCount =  messageService.findLetterUnreadCount(hostHolder.getUser().getId(),null);
            modelAndView.addObject("allUnreadCount",noticeUnreadCount + letterUnreadCount);
        }
    }

}
