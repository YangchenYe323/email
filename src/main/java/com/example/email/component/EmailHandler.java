package com.example.email.component;

import com.example.email.Util.EmailProcessUtil;
import com.example.email.domain.Mail;
import com.example.email.domain.UserInfo;
import com.sun.mail.pop3.POP3SSLStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * 封装了javamail API的邮箱服务器访问对象，使用流程：
 * 初始化（提供用户邮箱配置和协议类型（收件pop3，发件smtp） ->
 * 建立连接 ->
 * 收件方法获取Mail对象/发件方法发送邮件->
 * 关闭连接
 */
@Component
public class EmailHandler {
    /**
     * 初始化信息：用户邮箱配置
     */
    private UserInfo userInfo;

    URLName url;
    Session session;
    Store store;
    Transport transport;

    /**
     * 初始化
     * @param userInfo
     */
    public void initialize(UserInfo userInfo){
        this.userInfo = userInfo;
        //初始化特性设置
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "pop3");
        props.setProperty("mail.pop3.host", userInfo.getServer());
        props.setProperty("mail.pop3.port", String.valueOf(userInfo.getPort()));
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.port", String.valueOf(userInfo.getPort()));

        //连接地址
        url = new URLName("pop3", userInfo.getServer(), userInfo.getPort(), "", userInfo.getUsername(), userInfo.getPassword());
        session = Session.getInstance(props);
        store = new POP3SSLStore(session, url);
    }

    /**
     * 建立连接
     * @throws MessagingException
     */
    public void connect() throws MessagingException {
        store.connect();
    }

    /**
     * 收取该用户收件箱内指定日期之后收到的邮件
     * @Param threshold 指定日期，只获取更近收到的邮件
     * @return
     */
    public List<Mail> getNewMessages(Date threshold) throws MessagingException, IOException {
        List<Mail> receivedMails = new ArrayList<>();

        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_ONLY);
        Message[] messages = folder.getMessages();

        for (Message msg: messages){
            if (threshold == null || threshold.before(msg.getSentDate())){
                //获取邮件正文信息
                String username = userInfo.getUsername();
                String subject = msg.getSubject();
                String sender = ((InternetAddress)msg.getFrom()[0]).getAddress();
                StringBuffer contentBuffer = new StringBuffer();
                EmailProcessUtil.getMailTextContent(msg, contentBuffer);
                String content = contentBuffer.toString();
                //处理邮件附件
                List<String> attachmentPaths = new ArrayList<>();
                if(EmailProcessUtil.isContainAttachment(msg)){
                    String destDir = "./attachments/" + username + "/";
                    EmailProcessUtil.saveAttachment(msg, destDir, attachmentPaths);
                }
                //邮件对象
                Mail mail = new Mail();
                mail.setUsername(username);
                mail.setSender(sender);
                mail.setSubject(subject);
                mail.setContent(content);
                mail.setSentDate(msg.getSentDate());
                mail.setPaths(attachmentPaths);

                receivedMails.add(mail);
            }
        }

        folder.close();

        return receivedMails;
    }

    public void close() throws MessagingException {
        store.close();
        userInfo = null;
        session = null;
        store = null;
    }


}
