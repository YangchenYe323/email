package com.example.email.service;

import com.example.email.domain.MailLastDate;
import com.example.email.domain.UserInfo;
import com.example.email.mapper.LastDateMapper;
import com.example.email.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.Store;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 邮箱模块的主服务类，接收用户传来的配置信息并写入用户数据库
 */
@Service
public class EmailService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    StoreService storeService;

    public void insertUserInfo(UserInfo userInfo){
        userInfoMapper.insert(userInfo);
    }

    public void test(){
    }
}
