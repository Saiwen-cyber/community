package com.fangzhe.community.controller;

import com.fangzhe.community.entity.User;
import com.fangzhe.community.service.UserService;
import com.fangzhe.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fang
 */
@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    UserService userService;
    @GetMapping("/login")
    public String login(){
        return "site/login";
    }

    @GetMapping("/register")
    public String toRegisterPage(){
        return "site/register";
    }

    @PostMapping("/register")
    public String register(Model model, User user){
        Map<String,Object> map = new HashMap<String, Object>();
        map = userService.register(user);
        if(map == null || map.isEmpty()){
            model.addAttribute("msg","注册成功，已经发送邮件，请尽快激活");
            model.addAttribute("target","/index");
            return "site/operate-result";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "site/register";
        }
    }
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId,
                             @PathVariable("code") String code){
        int result = userService.activation(userId,code);
        if(result == ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功，正常使用!");
            model.addAttribute("target","/login");
        }else if(result == ACTIVATION_REAPEAT){
            model.addAttribute("msg","无效操作，已经激活过了");
            model.addAttribute("target","/index");
        }else {
            model.addAttribute("msg","激活失败，激活码错误");
            model.addAttribute("target","/index");
        }

        return "site/operate-result";
    }
}
