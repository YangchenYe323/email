package com.example.email.service;

import com.example.email.domain.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 邮箱模块的主服务类，接收用户传来的配置信息并写入用户数据库
 */
@Service
public class EmailService {

    @Autowired
    StoreService storeService;

    public void insertUserInfo(UserInfo userInfo){
        storeService.insertUserInfo(userInfo);
    }

}
