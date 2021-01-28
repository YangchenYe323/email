package com.example.email.task;

import com.example.email.constants.EmailConstants;
import com.example.email.domain.Mail;
import com.example.email.domain.UserInfo;
import com.example.email.mapper.MailMapper;
import com.example.email.mapper.UserInfoMapper;
import com.example.email.service.EmailFetchService;
import com.example.email.service.EmailStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 *
 */
@Component
public class EmailTask {


    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    MailMapper mailMapper;
    @Autowired
    EmailStoreService emailStoreService;
    @Autowired
    EmailFetchService emailFetchService;
    //当前轮次
    int turn = 0;

    /**
     *
     */
    @Scheduled(cron = "0 * 0/1 * * * ? ")
    public void getAndStoreEmail() {
        System.out.println("执行");
        //获取用户信息
        List<UserInfo> users = userInfoMapper.getAll();
        for(UserInfo user: users){
            //每分钟只处理该轮次的用户邮件
            if (user.hashCode() % EmailConstants.TIME_DELAY == turn){
                try{
                    //获取已经存储的最新邮件时间，只有更新的才需要处理
                    Date date = mailMapper.getLastSentDateFor(user.getUsername());
                    List<Mail> mails = emailFetchService.getMailFor(user, date);
                    for (Mail mail: mails){
                        emailStoreService.save(mail);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        //更新轮次
        turn = (turn + 1) % EmailConstants.TIME_DELAY;
    }
}