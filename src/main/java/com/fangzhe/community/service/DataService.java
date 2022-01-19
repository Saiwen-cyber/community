package com.fangzhe.community.service;

import com.fangzhe.community.util.RedisKeyUtil;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author fang
 */
@Service
public class DataService {
    @Autowired
    RedisTemplate redisTemplate;

    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 将单日UV记录下来
     */
    public void recordUV(String ip){
        String redisKey = RedisKeyUtil.getUVKey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey,ip);
    }
    /**
     * 统计指定范围内的UV
     */
    public long calculateUV(Date start, Date end){

        String redisKey = RedisKeyUtil.getUVKey(df.format(start), df.format(end));

        if(start == null || end == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        if (start.after(end)) {
            throw new IllegalArgumentException("请输入正确的时间段");
        }
        //整理日期范围内的Key;
        List<String> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while(!calendar.getTime().after(end)){
            String key = RedisKeyUtil.getUVKey(df.format(calendar.getTime()));
            keyList.add(key);
            //加一天
            calendar.add(Calendar.DATE, 1);
        }
        //union keyList 合并日期区间内的所有UV数据
        Long result = redisTemplate.opsForHyperLogLog().union(redisKey,keyList.toArray());
        return result;
    }

    /**
     * 将单日DAU记录下来
     */
    public void recordDAU(Integer id){
        String redisKey = RedisKeyUtil.getDAUKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey,id,true);
    }
    /**
     * 统计指定范围内的DAU
     * 一个用户是占位，在指定日期区间。
     * 举例：
     * date1 00010（userID为3的用户连着访问2天）
     * date2 00010
     * 区间的每一天的  该用户的 占位 做或运算，有一次就算。
     */
    public long calculateDAU(Date start, Date end){
        String redisKey = RedisKeyUtil.getDAUKey(df.format(start), df.format(end));

        if(start == null || end == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        if (start.after(end)) {
            throw new IllegalArgumentException("请输入正确的时间段");
        }
        //整理日期范围内的Key;
        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while(!calendar.getTime().after(end)){
            String key = RedisKeyUtil.getDAUKey(df.format(calendar.getTime()));
            keyList.add(key.getBytes());
            //加一天
            calendar.add(Calendar.DATE, 1);
        }
        return (long)redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.bitOp(
                        RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(),
                        keyList.toArray(new byte[0][0]));
                return connection.bitCount(redisKey.getBytes());
            }
        });

    }

}
