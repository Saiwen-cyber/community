package com.fangzhe.community.controller.interceptor;

import com.fangzhe.community.service.DataService;
import com.fangzhe.community.service.MessageService;
import com.fangzhe.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;

/**
 * @author fang
 */
@Component
public class DataInterceptor implements HandlerInterceptor {

    @Autowired
    DataService dataService;
    @Autowired
    HostHolder hostHolder;
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //统计UV
        String ip = request.getRemoteHost();
        dataService.recordUV(ip);
        if(hostHolder.getUser() != null && modelAndView != null){
            dataService.recordDAU(hostHolder.getUser().getId());
            modelAndView.addObject("allUnreadCount", null);
        }
    }

}
