package com.example.email.component;

import com.example.email.Util.EmailProcessUtil;
import com.example.email.domain.Mail;
import com.example.email.domain.UserInfo;
import com.sun.mail.smtp.SMTPSSLTransport;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
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

    public void sendMessage(Mail mail) throws Exception {
        //判断有无附件
        if (mail.getPaths() == null || mail.getPaths().isEmpty()){
            sendTextMessage(mail.getReceiverName(), mail.getSubject(), mail.getContent());
        } else{
            sendMessageWithAttachment(mail);
        }
    }

    public void sendTextMessage(String receiver, String subject, String content) throws Exception {
        MimeMessage message = EmailProcessUtil.createMimeMessage(session, userInfo.getUserName(), receiver, subject, content);
        transport.sendMessage(message, message.getAllRecipients());
    }

    public void sendMessageWithAttachment(Mail mail) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(mail.getSenderName());
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(mail.getReceiverName(), mail.getReceiverName(), "UTF-8"));
        message.setSubject(mail.getSubject());

        //总邮件体，multipart/mixed类型
        //邮件体结构: 总邮件体 -> [正文邮件体，...附件邮件体]
        MimeMultipart multipart = new MimeMultipart("mixed");
        //正文邮件体
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(mail.getContent(), "text/html");
        multipart.addBodyPart(textPart);
        //扫描附件
        for(String attachmentPath: mail.getPaths()){
            MimeBodyPart attachmentPart = new MimeBodyPart();
            DataSource ds = new FileDataSource(attachmentPath);
            DataHandler handler = new DataHandler(ds);
            attachmentPart.setDataHandler(handler);
            attachmentPart.setFileName(MimeUtility.encodeText(getFileName(attachmentPath)));
            multipart.addBodyPart(attachmentPart);
        }
        message.setContent(multipart);
        transport.sendMessage(message, message.getAllRecipients());
    }

    private String getFileName(String attachmentPath) {
        return attachmentPath.substring(attachmentPath.lastIndexOf('/') + 1);
    }

    public void close() throws MessagingException {
        transport.close();
    }

}
