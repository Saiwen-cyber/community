package com.fangzhe.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Properties;

/**
 * @author fang
 */
@Configuration
public class WkConfig {
    private static final Logger logger = LoggerFactory.getLogger(WkConfig.class);

    @Value("${wk.image.storage}")
    private String wkImageStore;
    @PostConstruct
    public void init(){
        //创建WK图片存取目录
        File file = new File(wkImageStore);
        if(!file.exists()){
            file.mkdir();
            logger.info("创建WK图片存取目录" + wkImageStore);
        }

    }
}
