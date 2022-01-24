package com.fangzhe.community.controller;

import com.fangzhe.community.entity.DiscussPost;
import com.fangzhe.community.entity.Page;
import com.fangzhe.community.entity.User;
import com.fangzhe.community.service.DiscussPosService;
import com.fangzhe.community.service.LikeService;
import com.fangzhe.community.service.UserService;
import com.fangzhe.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fang
 */
@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    private DiscussPosService discussPosService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @GetMapping("/")
    public String toIndex(){
        return "forward:/index";
    }

    @GetMapping("/index")
    public String getIndexPage(Model model, Page page,
                               @RequestParam(name = "orderMode",defaultValue = "0") int orderMode){
        //spring mvc 会自动实例化model，且会将page注入model
        //thymeleaf可以直接访问到page
        //系统博客总行数
        page.setRows(discussPosService.findDiscussPostRows(0));
        page.setPath("/index?orderMode="+orderMode);

        List<DiscussPost> list = discussPosService.findDiscussPosts(0, page.getOffset(), page.getLimit(), orderMode);
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(list!=null){
            for (DiscussPost post: list) {
                Map<String,Object> map = new HashMap<>();
                //添加user和post
                map.put("post",post);

                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);

                User user = userService.findUserById(post.getUserId());
                map.put("user",user);
                discussPosts.add(map);
            }
        }

        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("orderMode",orderMode);
        return "/index";
    }
    @GetMapping("/error")
    public String getErrorPage(){
        return "error/500";
    }

    /**
     * 拒绝访问提示页面
     */
    @GetMapping("/denied")
    public String getDeniedPage(){
        return "error/404";
    }
}
