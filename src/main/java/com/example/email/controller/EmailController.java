package com.example.email.controller;

import com.example.email.domain.Mail;
import com.example.email.domain.UserInfo;
import com.example.email.service.EmailClientService;
import com.example.email.service.TestService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
public class EmailController {

    @Autowired
    TestService testService;

    @Autowired
    EmailClientService emailClientService;

    @RequestMapping("/insert")
    @ResponseBody
    public String insert(){
        Mail mail = new Mail();
        mail.setReceiverName("yye02@email.wm.edu");
        mail.setSenderName("1320117484@qq.com");
        mail.setSubject("h");
        mail.setContent("bbb");
        return "P";
    }

    @RequestMapping("/send")
    @ResponseBody
    public String send(){
        System.out.println((new File("")).getAbsolutePath());
        UserInfo user = new UserInfo();
        user.setUserName("yangchen323@sina.com");
        user.setPassword("4f6c9ead7a16ba31");
        user.setPopServer("pop.sina.com");
        user.setPopPort(995);
        user.setSmtpServer("smtp.sina.com");
        user.setSmtpPort(465);


        Mail mail = new Mail();
        mail.setReceiverName("yye02@email.wm.edu");
        mail.setSenderName(user.getUserName());
        mail.setSubject("我喜欢你，和我在一起好不好");
        mail.setContent("愚人节快乐");
        List<String> paths = new ArrayList<>();
        paths.add("./src/main/resources/images/IMG_2145.JPG");
        mail.setPaths(paths);
        try {
            emailClientService.sendMail(user, mail);
        } catch (Exception e){
            e.printStackTrace();
        }

        return "Fuck";
    }

    @RequestMapping("/")
    @ResponseBody
    public String get(){
        try{
            //testService.sendDummyMessage();
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return "Success";
    }

    @RequestMapping("/upload")
    @ResponseBody
    public String uploadUserInfo(@RequestParam(value = "uname") String username,
                                 @RequestParam(value = "pword") String password,
                                 @RequestParam(value = "server") String server,
                                 @RequestParam(value = "port") int port){

        UserInfo userInfo = new UserInfo();
        userInfo.setUserName(username);
        userInfo.setPassword(password);
        userInfo.setPopServer(server);
        userInfo.setPopPort(port);


        return "Success";
    }

}
