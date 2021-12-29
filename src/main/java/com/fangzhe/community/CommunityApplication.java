package com.fangzhe.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import javax.annotation.PostConstruct;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class CommunityApplication {

    /**bean的初始化*/
    @PostConstruct
    public void init(){
        //解决netty启动冲突的问题 redis、es
        //see Netty4Utils.setAvailableProcessors()
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }
    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

}
