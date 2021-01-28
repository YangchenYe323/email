package com.example.email.controller;

import com.example.email.domain.Mail;
import com.example.email.domain.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class EmailController {

    @RequestMapping("/insert")
    @ResponseBody
    public String insert(){
        Mail mail = new Mail();
        mail.setUsername("yye02@email.wm.edu");
        mail.setSender("1320117484@qq.com");
        mail.setSubject("h");
        mail.setContent("bbb");
        return "P";
    }

    @RequestMapping("/")
    @ResponseBody
    public String get(){
        return "Success";
    }

    @RequestMapping("/upload")
    @ResponseBody
    public String uploadUserInfo(@RequestParam(value = "uname") String username,
                                 @RequestParam(value = "pword") String password,
                                 @RequestParam(value = "server") String server,
                                 @RequestParam(value = "port") int port){

        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(username);
        userInfo.setPassword(password);
        userInfo.setServer(server);
        userInfo.setPort(port);


        return "Success";
    }

}
