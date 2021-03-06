package com.fangzhe.community.event;

import com.alibaba.fastjson.JSONObject;
import com.fangzhe.community.entity.DiscussPost;
import com.fangzhe.community.entity.Event;
import com.fangzhe.community.entity.Message;
import com.fangzhe.community.service.DiscussPosService;
import com.fangzhe.community.service.ElasticsearchService;
import com.fangzhe.community.service.MessageService;
import com.fangzhe.community.util.CommunityConstant;
import com.fangzhe.community.util.CommunityUtil;
import com.google.gson.JsonObject;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author fang
 */
@Component
public class EventConsumer implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.share.name}")
    private String shareBucketName;

    @Value("${qiniu.bucket.share.url}")
    private String shareBucketUrl;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private MessageService messageService;

    @Autowired
    private DiscussPosService discussPosService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Value("${wk.image.command}")
    private String wkImageCommand;

    @Value("${wk.image.storage}")
    private String wkImageStore;

    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_FOLLOW, TOPIC_LIKE})
    public void handleCommentMessage(ConsumerRecord record){
        if(record == null || record.value() == null){
            logger.error("????????????????????????");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            logger.error("?????????????????????");
            return;
        }

        //??????????????????
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());
        //??????
        message.setStatus(0);
        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());

        if(!event.getData().isEmpty()){
            for (Map.Entry<String, Object> entry: event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);

    }
    //??????????????????
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMeassage(ConsumerRecord record){

        if(record == null || record.value() == null){
            logger.error("????????????????????????");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            logger.error("?????????????????????");
            return;
        }
        DiscussPost post = discussPosService.findDiscussPostById(event.getEntityId());
        elasticsearchService.saveDiscussPost(post);
    }
    //????????????????????????
    @KafkaListener(topics = {TOPIC_BLOCK})
    public void handleBlockMeassage(ConsumerRecord record){

        if(record == null || record.value() == null){
            logger.error("????????????????????????");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            logger.error("?????????????????????");
            return;
        }
        elasticsearchService.deleteDiscussPost(event.getEntityId());
    }


    /**
     * ??????share Image??????
     */
    @KafkaListener(topics = {TOPIC_SHARE})
    public void handleShareMessage(ConsumerRecord record){

        if(record == null || record.value() == null){
            logger.error("????????????????????????");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            logger.error("?????????????????????");
            return;
        }

        String htmlUrl = (String) event.getData().get("htmlUrl");
        String fileName = (String) event.getData().get("fileName");
        String suffix = (String) event.getData().get("suffix");

        String cmd = wkImageCommand + " --quality 75 "
                +htmlUrl + " " + wkImageStore + "/" + fileName + suffix;

        try {
            Runtime.getRuntime().exec(cmd);
            logger.info("??????????????????" + cmd);
        } catch (IOException e) {
            logger.error("??????????????????" + e.getMessage());
        }

        //????????????????????????????????????????????????????????????
        UploadTask uploadTask = new UploadTask(fileName, suffix);
        Future future = taskScheduler.scheduleAtFixedRate(uploadTask, 500);
        uploadTask.setFuture(future);
    }
    class UploadTask implements Runnable{

        //????????????
        private String fileName;
        //????????????
        private String suffix;
        //????????????????????????????????????
        private Future future;
        //????????????
        private long startTime;
        //???????????? 3???
        private int uploadTime;


        public UploadTask(String fileName, String suffix) {
            this.fileName = fileName;
            this.suffix = suffix;
            this.startTime = System.currentTimeMillis();
        }

        public void setFuture(Future future) {
            this.future = future;
        }

        @Override
        public void run() {
            //????????????
            if(System.currentTimeMillis() - startTime > 30000){
                logger.error("?????????????????????????????????" + fileName);
                future.cancel(true);
                return;
            }
            //????????????
            if(uploadTime >= 3){
                logger.error("?????????????????????????????????" + fileName);
                future.cancel(true);
                return;
            }
            String path = wkImageStore + "/" + fileName + suffix;
            File file = new File(path);
            if(file.exists()){
                logger.info(String.format("?????????%d?????????[%s].",++uploadTime,fileName));
                //??????????????????
                StringMap policy = new StringMap();
                policy.put("returnBody", CommunityUtil.getJSONString(0));
                //??????????????????
                Auth auth = Auth.create(accessKey, secretKey);
                String upToken = auth.uploadToken(shareBucketName, fileName, 3600, policy);
                //??????????????????
                UploadManager manager = new UploadManager(new Configuration(Zone.zone1()));
                try {
                    //??????????????????
                    Response r = manager.put(path, fileName, upToken, null, "/image/" + suffix,false);
                    //??????????????????
                    JSONObject json = JSONObject.parseObject(r.bodyString());
                    if(json == null || json.get("code") == null ||
                    !json.get("code").toString().equals("0")){
                        logger.info(String.format("???%d?????????[%s]??????.",uploadTime,fileName));
                    }else {
                        logger.info(String.format("???%d?????????[%s]??????.",uploadTime,fileName));
                        future.cancel(true);
                    }
                }catch (QiniuException e){
                    logger.info(String.format("???%d?????????[%s]??????.",uploadTime,fileName));
                }
            }else {
                logger.info(String.format("????????????[%s]??????.",fileName));
            }
        }
    }

}
