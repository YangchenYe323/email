package com.example.email.component;

import com.example.email.Util.EmailProcessUtil;
import com.example.email.domain.Mail;
import com.example.email.domain.UserInfo;
import com.sun.mail.smtp.SMTPSSLTransport;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * 封装了javamail发送邮件的API
 * 调用流程：
 * 初始化（接收用户邮箱配置对象，初始化javamail对象）->
 * 开启连接 ->
 * 调用发件方法（纯文本、带附件邮件） ->
 * 关闭连接释放资源
 */
public class EmailSender {

    /**
     * 初始化信息：用户邮箱配置
     */
    private UserInfo userInfo;

    URLName url;
    Session session;
    Transport transport;

    public void initialize(UserInfo userInfo){
        this.userInfo = userInfo;
        //初始化smtp特性对象
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", userInfo.getSmtpServer());
        props.setProperty("mail.smtp.port", String.valueOf(userInfo.getSmtpPort()));
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.port", String.valueOf(userInfo.getSmtpPort()));

        session = Session.getInstance(props);
        url = new URLName("smtp", userInfo.getSmtpServer(), userInfo.getSmtpPort(), "", userInfo.getUserName(), userInfo.getPassword());
        transport = new SMTPSSLTransport(session, url);
    }

    public void connect() throws MessagingException {
        transport.connect();
    }

    public void sendTextMessage(String receiver, String subject, String content) throws Exception {
        MimeMessage message = EmailProcessUtil.createMimeMessage(session, userInfo.getUserName(), receiver, subject, content);
        transport.sendMessage(message, message.getAllRecipients());
    }

    public void sendMessageWithAttachment(Mail mail){

    }

    public void close() throws MessagingException {
        transport.close();
    }

}
