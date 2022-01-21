package com.fangzhe.community.quartz;

import com.fangzhe.community.entity.DiscussPost;
import com.fangzhe.community.entity.Event;
import com.fangzhe.community.entity.User;
import com.fangzhe.community.event.EventProducer;
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
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * 更新 post:score (redisKey) 所有post的score
 * @author fang
 */
public class PostScoreRefreshJob implements Job, CommunityConstant {
    private final static Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    DiscussPosService discussPosService;
    @Autowired
    LikeService likeService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    ElasticsearchService elasticsearchService;
    private static final Date epoch;
    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2001-12-05 00:00:00");
        } catch (ParseException exception) {
            throw new RuntimeException("初始化牛客纪元失败！",exception);
        }
    }


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScore();
//        Set<Integer> set = redisTemplate.opsForSet().members(redisKey);

        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);
        if(operations.size() == 0){
            logger.info("[定时任务取消] 没有需要刷新的帖子");
            return;
        }
        logger.info("[定时任务开始] 正在刷新帖子分数:" + operations.size());
//        for (Integer id : set) {
//            refresh(id);
//        }
        while (operations.size() > 0) {
            this.refresh((Integer) operations.pop());
        }
        logger.info("[定时任务完成] 刷新完毕帖子分数:" + operations.size());

    }
    private void refresh(int id){
        DiscussPost post = discussPosService.findDiscussPostById(id);
        if(post == null){
            logger.error("该帖子不存在：id = "+id);
        }
        boolean essence = post.getStatus() == 1;
        double likeCount = Double.valueOf(likeService.findEntityLikeCount(ENTITY_TYPE_POST,id));
        int commentCount = post.getCommentCount();
        //权重
        double weight = (essence? 75 : 0) + commentCount * 10 + likeCount * 2;
        //分数 = 帖子权重 + 距离天数
        double score = Math.log10(Math.max(weight,1))
                +(post.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);
        //更新帖子分数
        discussPosService.updateScore(id,score);
        //同步 es 与 mysql
        post.setScore(score);
        elasticsearchService.saveDiscussPost(post);
    }
}
