package com.example.email.service;

import com.example.email.component.EmailHandler;
import com.example.email.domain.Mail;
import com.example.email.domain.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 处理用户邮箱配置 -> 访问服务器获取/发送邮件的业务逻辑
 */
@Service
public class EmailFetchService {

    @Autowired
    EmailHandler emailHandler;

    /**
     * 获取指定用户所有邮件
     * @param userInfo
     * @return
     * @throws IOException
     * @throws MessagingException
     */
    public List<Mail> getMailFor(UserInfo userInfo) throws IOException, MessagingException {
        return getMailFor(userInfo, null);
    }

    /**
     * 获取指定用户在指定日期之后的所有新邮件
     * @param userInfo
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public List<Mail> getMailFor(UserInfo userInfo, Date date) throws MessagingException, IOException {
        emailHandler.initialize(userInfo);
        emailHandler.connect();
        List<Mail> result = emailHandler.getNewMessages(date);
        emailHandler.close();
        return result;
    }

}
