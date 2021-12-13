package com.fangzhe.community.controller;

import com.fangzhe.community.entity.DiscussPost;
import com.fangzhe.community.entity.Page;
import com.fangzhe.community.entity.User;
import com.fangzhe.community.service.DiscussPosService;
import com.fangzhe.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fang
 */
@Controller
public class HomeController {
    @Autowired
    private DiscussPosService discussPosService;

    @Autowired
    private UserService userService;

    @GetMapping("/index")
    public String getIndexPage(Model model, Page page){
        //spring mvc 会自动实例化model，且会将page注入model
        //thymeleaf可以直接访问到page
        //系统博客总行数
        page.setRows(discussPosService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> list = discussPosService.findDiscussPosts(0,page.getOffset(),page.getLimit());
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(list!=null){
            for (DiscussPost post: list) {
                Map<String,Object> map = new HashMap<>();
                //添加user和post
                map.put("post",post);

                User user = userService.findUserById(post.getUserId());
                map.put("user",user);
                discussPosts.add(map);
            }
        }

        model.addAttribute("discussPosts",discussPosts);
        return "/index";
    }
    @GetMapping("/error")
    public String getErrorPage(){
        return "error/500";
    }
}
