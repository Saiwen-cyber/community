package com.fangzhe.community.quartz;

import com.fangzhe.community.entity.DiscussPost;
import com.fangzhe.community.service.DiscussPosService;
import com.fangzhe.community.service.ElasticsearchService;
import com.fangzhe.community.service.LikeService;
import com.fangzhe.community.util.CommunityConstant;
import com.fangzhe.community.util.HostHolder;
import com.fangzhe.community.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

/**
 * 启动定时任务（每4分钟执行一次）
 * 删除1分钟之前由分享功能所创建的临时文件。
 * @author fang
 */
public class DeleteTmpShareImageJob implements Job, CommunityConstant {
    private final static Logger logger = LoggerFactory.getLogger(DeleteTmpShareImageJob.class);

    @Value("${wk.image.storage}")
    private String wkImageStore;

    /**
     * 删除1分钟之前由分享功能所创建的临时文件。
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        File dir = new File(wkImageStore);
        File[] files = dir.listFiles();
        //判断时间？
        if(files == null){
            logger.info("[定时任务取消] 没有需要删除的临时文件.");
            return;
        }
        int count = 0;
        logger.info("[定时任务开始] 正在删除临时图片.");
        for (File file: files) {
            if(System.currentTimeMillis() - file.lastModified() > 1000*60){
                boolean flag = file.delete();
                if(flag){ count++;}
            }
        }
        logger.info("[定时任务完成] 删除临时图片完成:" + count);
    }
}

