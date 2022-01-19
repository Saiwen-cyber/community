package com.fangzhe.community.controller;

import com.fangzhe.community.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.Date;

/**
 * @author fang
 */
@Controller
@RequestMapping("/data")
public class DataController {
    @Autowired
    DataService dataService;

    @PostMapping("/uv")
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd")Date uvStartDate,
                        @DateTimeFormat(pattern = "yyyy-MM-dd")Date uvEndDate,
                        Model model){

            model.addAttribute("uvStart", uvStartDate);
            model.addAttribute("uvEnd", uvEndDate);
            model.addAttribute("uvCount",dataService.calculateUV(uvStartDate, uvEndDate));
        return "forward:/data/page";
    }
    @PostMapping("/dau")
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd")Date  dauStartDate,
                         @DateTimeFormat(pattern = "yyyy-MM-dd")Date  dauEndDate,
                         Model model){

            model.addAttribute("dauStart", dauStartDate);
            model.addAttribute("dauEnd", dauEndDate);
            model.addAttribute("dauCount",dataService.calculateDAU(dauStartDate, dauEndDate));
            //复用getData段逻辑
            return "forward:/data/page";
    }

    @RequestMapping(value = "/page",method = {RequestMethod.POST,RequestMethod.GET})
    public String getData(){
        return "/site/admin/data";
    }
}
