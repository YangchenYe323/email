package com.example.email.service;

import com.example.email.component.EmailFetcher;
import com.example.email.component.EmailSender;
import com.example.email.domain.Mail;
import com.example.email.domain.UserInfo;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 处理本应用与用户邮箱服务器交互的业务逻辑
 */
@Service
public class EmailClientService {


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
        EmailFetcher emailFetcher = new EmailFetcher();
        emailFetcher.initialize(userInfo);
        emailFetcher.connect();
        List<Mail> result = emailFetcher.getNewMessages(date);
        emailFetcher.close();
        return result;
    }

    /**
     * 发送邮件
     * @param userInfo
     * @param mail
     * @throws Exception
     */
    public void sendMail(UserInfo userInfo, Mail mail) throws Exception {
        EmailSender emailSender = new EmailSender();
        emailSender.initialize(userInfo);
        emailSender.connect();
        //判断邮件是否有附件
        emailSender.sendMessage(mail);
        emailSender.close();
    }

}
