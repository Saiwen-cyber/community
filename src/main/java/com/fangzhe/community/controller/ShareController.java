package com.fangzhe.community.controller;

import com.fangzhe.community.entity.Event;
import com.fangzhe.community.event.EventProducer;
import com.fangzhe.community.util.CommunityConstant;
import com.fangzhe.community.util.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fang
 */
@Controller
public class ShareController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(ShareController.class);

    @Value("${qiniu.bucket.share.url}")
    private String shareBucketUrl;

    @Value("${wk.image.storage}")
    private String wkImageStore;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private EventProducer eventProducer;

    @GetMapping("/share")
    @ResponseBody
    public String share(String htmlUrl){
        String fileName =  CommunityUtil.generateUUId() ;

        Event event = new Event()
                .setTopic(TOPIC_SHARE)
                .setData("htmlUrl", htmlUrl)
                .setData("fileName", fileName)
                .setData("suffix",".png");
        eventProducer.fireEvent(event);

        //返回访问路径
        Map<String, Object> map = new HashMap<>();
        map.put("shareUrl", shareBucketUrl +  "/" + fileName);

        return CommunityUtil.getJSONString(0,null,map);
    }

    /**
     * 废弃
     * 获取长图
     */
    @GetMapping("/share/image/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        if(StringUtils.isBlank(fileName)){
            throw  new IllegalArgumentException("文件名不能为空！");
        }
        response.setContentType("image/png");
        File file = new File(wkImageStore + "/" + fileName + ".png");
        try (
                FileInputStream fis = new FileInputStream(file);
                OutputStream os = response.getOutputStream();
        ){
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取长图失败" + e.getMessage());
        }
    }
}
