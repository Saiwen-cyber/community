package com.fangzhe.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

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
}
