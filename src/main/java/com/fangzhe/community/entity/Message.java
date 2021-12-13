package com.fangzhe.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author fang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    Integer id;
    Integer fromId;
    Integer toId;
    String conversationId;
    String content;
    Integer status;
    Date createTime;
}
