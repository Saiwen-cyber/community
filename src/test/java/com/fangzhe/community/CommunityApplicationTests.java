package com.fangzhe.community;

import ch.qos.logback.core.net.server.Client;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.sql.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests {

    @Autowired
    MessageMapper messageMapper;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    DiscussPostMapper discussPostMapper;
    @Autowired
    DiscussPosService discussPosService;
    @Autowired
    UserService userService;
    @Autowired
    MailClient client;
    @Autowired
    TemplateEngine templateEngine;
    @Autowired
    private SensitiveFilter sensitiveFilter;


    @Test
    void contextLoads() {
        List<DiscussPost> list = discussPosService.findDiscussPosts(103,0,10,0);
        for (DiscussPost po :list) {
            System.out.println(po);
        }
        User user = userService.findUserById(1);
        System.out.println(user.getUsername());
    }
    @Test
    public void sendMailTest(){
        client.sendJavaMail("1971650290@qq.com","test","你好");
    }
    @Test
    public void sendHtmlMailTest(){
        Context context = new Context();
        context.setVariable("username","roseanne");
        String content = templateEngine.process("/mail/welcome",context);

        client.sendJavaMail("1971650290@qq.com","test",content);
    }
    @Test
    public void testLoginTicketMapper(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(5);
        loginTicket.setTicket("mkwcmdoiw");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()));
//        loginTicketMapper.insertLoginTicket(loginTicket);
        loginTicketMapper.updateLoginTicket(loginTicket.getTicket(),1);
        System.out.println(loginTicketMapper.selectLoginTicket(loginTicket.getTicket()).getStatus());


    }
    @Test
    public void testSensitiveWord(){
        String test = "这里可以赌博、可以嫖娼、@@可以吸毒，可以开票哈哈哈哈哈";
        test = sensitiveFilter.filter(test);
        System.out.println(test);
    }
    @Test
    public void testInsertDiscussPost(){
        DiscussPost post = new DiscussPost();
        post.setUserId(888);
        discussPostMapper.insertDiscussPost(post);
    }
    @Test
    public void testMessageMapper(){
       List<Message> messages = messageMapper.selectConversations(111,0,20);
        for (Message message:messages) {
            System.out.println(message);
        }
        int count = messageMapper.selectConversationCount(111);
        int count2 = messageMapper.selectLetterUnreadCount(111,null);
        int count3 = messageMapper.selectLetterCount("111_112");
        System.out.println(count);
        System.out.println(count2);
        System.out.println(count3);
        List<Message> messages2 = messageMapper.selectLetters("111_112",0,20);
        for (Message message:messages2) {
            System.out.println(message);
        }
    }

}
