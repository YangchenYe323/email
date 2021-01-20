package com.example.email.component;

import com.example.email.domain.Attachment;
import com.example.email.domain.Mail;
import com.example.email.domain.MailLastDate;
import com.example.email.domain.UserInfo;
import com.example.email.mapper.UserInfoMapper;
import com.example.email.service.StoreService;
import com.sun.mail.pop3.POP3SSLStore;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;

/**
 *
 */
@Component
public class EmailTask {

    //存储服务
    @Autowired
    private StoreService storeService;
    //间隔十五分钟完成一次所有用户的
    //邮箱拉取，
    private static int TIME_DELAY = 15;

    //用于记录组数，将用户按照hashcode分成15组，
    //每分钟执行一组，以此分摊运行时间
    private int turn = 0;
    //用户邮箱配置
    private List<UserInfo> users;
    //《用户id - 该用户最新拉取的邮件收取日期》，用于只拉取
    //新收到的邮件
    private Map<Integer, Date> lastProcessedEmailDate;

    int count = 1;

    /**
     *
     */
    @Scheduled(cron = "0/30 * * * * ? ")
    public void getAndStoreEmail() {

        System.out.println("第" + count + "次执行Scheduled Task");

        //初始化阶段：从用户信息表中拉取用户邮箱配置
        System.out.println("数据库读取用户邮箱配置:");
        users = storeService.getAllUserInfo();
        for (UserInfo uInfo : users) {
            System.out.println("用户id： " + uInfo.getId());
            System.out.println("用户邮箱地址： " + uInfo.getUsername());
            System.out.println("用户授权码： " + uInfo.getPassword());
            System.out.println("用户邮箱pop3服务器地址： " + uInfo.getServer());
            System.out.println("用户邮箱pop3服务器端口号： " + uInfo.getPort());
        }
        //从时间表中拉取收件时间信息
        System.out.println("数据库读取用户最新已存邮件发送时间：");
        List<MailLastDate> dates = storeService.getAllDate();
        //使用此信息构建映射
        lastProcessedEmailDate = new HashMap<>();
        for (MailLastDate d: dates){
            lastProcessedEmailDate.put(d.getId(), d.getDate());
            System.out.println("用户id： " + d.getId() + ", 最新已存邮件发送时间： " + d.getDate());
        }

        for (UserInfo userInfo : users) {
            //一分钟内只处理一批次的用户
            if (userInfo.hashCode() % TIME_DELAY == turn) {
                try {
                    String username = userInfo.getUsername();
                    String password = userInfo.getPassword();
                    String server = userInfo.getServer();
                    int port = userInfo.getPort();

                    //初始化邮箱服务器连接
                    Properties props = new Properties();
                    props.setProperty("mail.transport.protocol", "pop3");
                    props.setProperty("mail.pop3.host", server);
                    props.setProperty("mail.pop3.port", String.valueOf(port));
                    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                    props.put("mail.smtp.socketFactory.port", String.valueOf(port));

                    URLName url = new URLName("pop3", server, port, "", username, password);
                    Session session = Session.getInstance(props, null);
                    session.setDebug(false);
                    Store store = new POP3SSLStore(session, url);
                    store.connect();//登录

                    Folder folder = store.getFolder("INBOX");
                    folder.open(Folder.READ_WRITE);

                    Message[] messages = folder.getMessages();

                    for (Message message : messages) {
                        //判断该邮件是否为新收到邮件，若是，则开始处理
                        Date threshold = lastProcessedEmailDate.get(userInfo.getId());
                        if (threshold == null || threshold.before(message.getSentDate())) {
                            //邮件主题
                            String subject = message.getSubject();
                            Address from = message.getFrom()[0];
                            //发件人地址
                            String sender = ((InternetAddress) from).getAddress();
                            //邮件内容
                            StringBuffer content = new StringBuffer();
                            getMailTextContent(message, content);

                            System.out.println("邮件的主题为: " + subject + "\t发件人地址为: " + sender);
                            System.out.println(message.getContentType());
                            System.out.println(message.isMimeType("multipart/*"));
                            System.out.println("邮件的内容为：");
                            System.out.println(content);
                            //message.writeTo(System.out);// 输出邮件内容到控制台

                            //邮件附件处理
                            //String dir = Paths.get(".").toAbsolutePath().toString();
                            //System.out.println(dir);
                            //每一个收件人的附件存储在分别的目录下
                            String destDir = "./attachments/" + username + "/";
                            File file = new File(destDir);
                            //TODO:目录逻辑
                            if (!file.exists()) {
                                file.mkdir();
                            }
                            List<String> attachmentPaths = new ArrayList<>();
                            if (isContainAttachment(message)) {
                                saveAttachment(message, destDir, attachmentPaths);
                                System.out.println("邮件附件路径：");
                                System.out.println(attachmentPaths);
                            }

                            //邮件对象
                            Mail mail = new Mail();
                            mail.setContent(content.toString());
                            mail.setSubject(subject);
                            mail.setSender(sender);
                            mail.setUsername(username);
                            int id = storeService.insertMail(mail);//此时已经获得了数据库自动生成的id

                            //附件对象
                            for (String path : attachmentPaths) {
                                Attachment attachment = new Attachment();
                                attachment.setId(id);
                                attachment.setPath(Paths.get(path).toAbsolutePath().toString());
                                //存储附件对象
                                storeService.insertAttachment(attachment);
                            }

                            //更新最后收件的时间戳数据库
                            MailLastDate mdate = new MailLastDate();
                            mdate.setId(userInfo.getId());
                            mdate.setDate(message.getSentDate());
                            if (threshold == null){
                                storeService.insertDate(mdate);
                            } else{
                                storeService.updateDate(mdate);
                            }
                            lastProcessedEmailDate.put(userInfo.getId(), message.getSentDate());

                        }

                    }
                    folder.close(false);// 关闭邮件夹对象
                    store.close(); // 关闭连接对象
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
            //组数轮换
            turn = (turn + 1) % TIME_DELAY;
        }

    }

    public static void getMailTextContent(Part part, StringBuffer content) throws MessagingException, IOException {
        //示例附件contentType: text/plain; charset="US-ASCII"; name="1.txt"
        //示例主文本contentType：text/plain; charset="UTF-8"
        //使用name属性区别出附件
        boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;
        if (part.isMimeType("text/plain") && !isContainTextAttach) {
            content.append(part.getContent().toString());
        } else if (part.isMimeType("message/rfc822")) {
            getMailTextContent((Part)part.getContent(),content);
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                /*System.out.println("---------------------------------------");
                System.out.println(bodyPart.getContentType());
                System.out.println(bodyPart.getDisposition());
                System.out.println("---------------------------------------");*/
                getMailTextContent(bodyPart,content);
            }
        }
    }

    public static boolean isContainAttachment(Part part) throws MessagingException, IOException {
        boolean flag = false;
        if (part.isMimeType("multipart/*")) {
            MimeMultipart multipart = (MimeMultipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                    flag = true;
                } else if (bodyPart.isMimeType("multipart/*")) {
                    flag = isContainAttachment(bodyPart);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.contains("application")) {
                        flag = true;
                    }

                    if (contentType.contains("name")) {
                        flag = true;
                    }
                }

                if (flag) break;
            }
        } else if (part.isMimeType("message/rfc822")) {
            flag = isContainAttachment((Part)part.getContent());
        }
        return flag;
    }

    public static void saveAttachment(Part part, String destDir, List<String> attachmentPaths) throws UnsupportedEncodingException, MessagingException,
            FileNotFoundException, IOException {
        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();    //复杂体邮件
            //复杂体邮件包含多个邮件体
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                //获得复杂体邮件中其中一个邮件体
                BodyPart bodyPart = multipart.getBodyPart(i);
                //某一个邮件体也有可能是由多个邮件体组成的复杂体
                //若如此，则disp为null，继续递归
                //若此邮件体已经是叶邮件体，判断类型是否为附件
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                    //为附件，存储到指定目录
                    String fileName = decodeText(bodyPart.getFileName());
                    InputStream is = bodyPart.getInputStream();
                    saveFile(is, destDir, fileName);
                    String filePath = destDir + fileName;
                    attachmentPaths.add(filePath);
                } else if (bodyPart.isMimeType("multipart/*")) {
                    saveAttachment(bodyPart,destDir, attachmentPaths);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.indexOf("name") != -1 || contentType.indexOf("application") != -1) {
                        saveFile(bodyPart.getInputStream(), destDir, decodeText(bodyPart.getFileName()));
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            saveAttachment((Part) part.getContent(),destDir, attachmentPaths);
        }
    }

    private static void saveFile(InputStream is, String destDir, String fileName)
            throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(new File(destDir + fileName)));
        int len = -1;
        while ((len = bis.read()) != -1) {
            bos.write(len);
            bos.flush();
        }
        bos.close();
        bis.close();
    }

    public static String decodeText(String encodeText) throws UnsupportedEncodingException {
        if (encodeText == null || "".equals(encodeText)) {
            return "";
        } else {
            return MimeUtility.decodeText(encodeText);
        }
    }

    public static Date getSentDate(Message msg) throws MessagingException {
        return msg.getSentDate();
    }

}
