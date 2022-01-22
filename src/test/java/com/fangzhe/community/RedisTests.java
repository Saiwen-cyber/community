package com.fangzhe.community;

import com.fangzhe.community.dao.DiscussPostMapper;
import com.fangzhe.community.dao.LoginTicketMapper;
import com.fangzhe.community.dao.MessageMapper;
import com.fangzhe.community.entity.DiscussPost;
import com.fangzhe.community.entity.LoginTicket;
import com.fangzhe.community.entity.Message;
import com.fangzhe.community.entity.User;
import com.fangzhe.community.service.DiscussPosService;
import com.fangzhe.community.service.UserService;
import com.fangzhe.community.util.MailClient;
import com.fangzhe.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.BoundKeyOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.sql.Date;
import java.util.List;
import java.util.Stack;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class RedisTests {
    @Autowired
    RedisTemplate redisTemplate;
    @Test
    public void testStrings(){
        String redisKey = "test:count";
        redisTemplate.opsForValue().set(redisKey,1);
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));


    }
    @Test
    public void testBound(){
        String redisKey = "test:count";
        //绑定redisKey的操作（Key - Value）
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
        operations.increment();
        System.out.println(operations.get());

    }
    //统计20万个重复数据的独立总数
    @Test
    public void testHyperLogLog(){
        String redisKey = "test:hll:01";

        for (int i = 0; i < 100000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey, i);
        }
        for (int i = 0; i < 100000; i++) {
            int r = (int) (Math.random() * 100000 + 1);
            redisTemplate.opsForHyperLogLog().add(redisKey, r);
        }
        Long result = redisTemplate.opsForHyperLogLog().size(redisKey);
        System.out.println(result);
    }
    //联合三组数据，得到去重后的独立总数
    @Test
    public void testHyperLogLogUnion(){
        String redisKey = "test:hll:union";
        String redisKey2 = "test:hll:02";
        String redisKey3 = "test:hll:03";
        String redisKey4 = "test:hll:04";

        for (int i = 0; i < 10000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey2, i);
        }
        for (int i = 5000; i < 15000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey3, i);
        }
        for (int i = 10000; i < 20000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey4, i);
        }
        Long result = redisTemplate.opsForHyperLogLog().union(redisKey,redisKey2,redisKey3,redisKey4);
        System.out.println(result);
    }
    //统计一组数据的布尔值
    @Test
    public void testBigMap(){
        String redisKey1 = "test:bm:01";

        redisTemplate.opsForValue().setBit(redisKey1,0,true);
        redisTemplate.opsForValue().setBit(redisKey1,2,true);
        redisTemplate.opsForValue().setBit(redisKey1,5,true);

        Boolean result1 = redisTemplate.opsForValue().getBit(redisKey1,5);
        System.out.println(result1);
        Object result2 = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount(redisKey1.getBytes());
            }
        });
        System.out.println(result2);
    }
    //统计3组数据的布尔值，进行OR运算
    @Test
    public void testBigMapOptions(){
        String redisKey = "test:bm:or";
        String redisKey2 = "test:bm:02";
        String redisKey3 = "test:bm:03";
        String redisKey4 = "test:bm:04";

        redisTemplate.opsForValue().setBit(redisKey2,0,false);
        redisTemplate.opsForValue().setBit(redisKey2,1,true);
        redisTemplate.opsForValue().setBit(redisKey2,2,true);

        redisTemplate.opsForValue().setBit(redisKey3,2,true);
        redisTemplate.opsForValue().setBit(redisKey3,3,true);
        redisTemplate.opsForValue().setBit(redisKey3,4,true);

        redisTemplate.opsForValue().setBit(redisKey4,4,true);
        redisTemplate.opsForValue().setBit(redisKey4,5,true);
        redisTemplate.opsForValue().setBit(redisKey4,6,true);

        Boolean result1 = redisTemplate.opsForValue().getBit(redisKey4,5);
        System.out.println(result1);
        Object result2 = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    connection.bitOp(
                        RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(),
                        redisKey2.getBytes(),redisKey3.getBytes(),redisKey4.getBytes());
                    return connection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(result2);
    }



}
