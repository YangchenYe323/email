package com.example.email.controller;

import com.example.email.domain.Mail;
import com.example.email.service.ReceiveService;
import com.example.email.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class EmailController {

    @Autowired
    StoreService emailService;

    @Autowired
    ReceiveService receiveService;

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
    public Mail get(){
        try{
            receiveService.getReceivedEmail("yangchen323@sina.com", "4f6c9ead7a16ba31", "pop3.sina.com", 995);
        } catch(Exception e){
            e.printStackTrace();
        }
        return emailService.getMail(1L);
    }

}
