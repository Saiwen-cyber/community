package com.fangzhe.community.controller;

import com.fangzhe.community.annotations.LoginRequired;
import com.fangzhe.community.entity.User;
import com.fangzhe.community.service.FollowService;
import com.fangzhe.community.service.LikeService;
import com.fangzhe.community.service.UserService;
import com.fangzhe.community.util.CommunityConstant;
import com.fangzhe.community.util.CommunityUtil;
import com.fangzhe.community.util.HostHolder;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author fang
 */
@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.header.name}")
    private String headerBucketName;

    @Value("${qiniu.bucket.header.url}")
    private String headerBucketUrl;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage(Model model){
        //??????????????????
        String fileName = CommunityUtil.generateUUId();
        //??????????????????
        StringMap policy = new StringMap();
        policy.put("returnBody",CommunityUtil.getJSONString(0));
        //??????????????????
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(headerBucketName, fileName, 3600, policy);

        model.addAttribute("upToken", upToken);
        model.addAttribute("fileName", fileName);

        return "/site/setting";
    }

    /**
     * ?????????????????????
     * http://r65svr0ge.hb-bkt.clouddn.com/xxx.png
     */
    @PostMapping("/header/url")
    @ResponseBody
    public String updateHeaderUrl(String fileName){
        if(fileName == null){
            return CommunityUtil.getJSONString(1,"????????????????????????");
        }
        User user = hostHolder.getUser();
        String headerUrl = headerBucketUrl + "/" +fileName;
        userService.updateHeader(user.getId(),headerUrl);

        return CommunityUtil.getJSONString(0);
    }

    /** ?????? */
    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model){
        if (headerImage == null){
            model.addAttribute("error","???????????????????????????");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".")+1);
        if (StringUtils.isBlank(suffix)){
            model.addAttribute("error","?????????????????????");
            return "/site/setting";
        }

        //?????????????????????
        fileName = CommunityUtil.generateUUId() + "."+suffix;
        //????????????????????????
        File dest = new File(uploadPath + "/" + fileName);
        try {
            //????????????
            headerImage.transferTo(dest);
        } catch (Exception e) {
            logger.error("??????????????????"+e.getMessage());
            throw new RuntimeException("??????????????????????????????????????????!" + e.getMessage());
        }

        //?????????????????????????????????web???????????????;
        //http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";
    }
    /**
     * ??????
     * ????????????
     */
    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //?????????????????????
        fileName = uploadPath + "/" + fileName;
        //??????????????????
        String suffix = fileName.substring(fileName.lastIndexOf(".")+1);
        response.setContentType("image/" + suffix);

        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ){
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("??????????????????" + e.getMessage());
        }
    }
    /**
     * ??????????????????
     */
    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("??????????????????!");
        }
        //???????????????
        model.addAttribute("user",user);
        //?????????????????????
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);
        //????????????
        long followeeCount = followService.findFolloweeCount(userId,ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        //????????????
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER,userId);
        model.addAttribute("followerCount",followerCount);
        //??????????????????
        boolean hasFollowed = false;
        if(hostHolder.getUser() != null){
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(),
                    ENTITY_TYPE_USER,
                    userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);
        return "site/profile";

    }


}
