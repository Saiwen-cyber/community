package com.fangzhe.community.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

/**
 * @author fang
 */
public class CommunityUtil {
    /**
    *生成随机字符串
     */
    public static String generateUUId(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
    /**
     * md5加密算法。
     * 密码加密，太过简单会被破解
     * 加上几位随机数，进行加密。
     */
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * 将数据转换为json字符串
     * @param code json编号
     * @param msg 封装提示信息
     * @param map 数据封装
     * @return
     */
    public static String getJSONString(int code, String msg, Map<String,Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if( map != null){
            for (String key:map.keySet()) {
                json.put(key,map.get(key));
            }
        }
        return json.toJSONString();
    }
    public static String getJSONString(int code){
        JSONObject json = new JSONObject();
        json.put("code",code);
        return json.toJSONString();
    }
    public static String getJSONString(int code, String msg){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        return json.toJSONString();
    }
}
