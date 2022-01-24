package com.fangzhe.community;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.LinkedList;

class DelFileTests {
    @Test
    public void test(){
        deleteFilesDir();
    }

    public void deleteFilesDir(){
        String wkImageStore = "d:/work/data/wk-images";
        File dir = new File(wkImageStore);
        File[] files = dir.listFiles();
        for (File file: files) {
            if(System.currentTimeMillis() - file.lastModified() > 1000*60){
                boolean flag = file.delete();
                if (flag){

                }
            }
        }
    }

}
