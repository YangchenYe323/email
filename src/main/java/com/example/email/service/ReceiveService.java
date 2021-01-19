package com.example.email.service;


import com.example.email.domain.Mail;
import com.example.email.domain.MailWithAttachment;
import com.sun.mail.pop3.POP3SSLStore;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class ReceiveService {

    @Autowired
    StoreService storeService;

    public MailWithAttachment getReceivedEmail(String username, String password, String server, int port) throws MessagingException, IOException {

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

        for (Message message: messages){
            //邮件主题
            String subject = message.getSubject();
            Address from = message.getFrom()[0];
            //发件人地址
            String sender = ((InternetAddress)from).getAddress();
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
            String dir = Paths.get(".").toAbsolutePath().toString();
            //System.out.println(dir);
            //每一个收件人的附件存储在分别的目录下
            String destDir = "./attachments/" + username + "/";
            File file = new File(destDir);
            if (!file.exists()){
                System.out.println(file.mkdir());
            }
            List<String> attachmentPaths = new ArrayList<>();
            if (isContainAttachment(message)){
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
            int id = storeService.insertMail(mail);

            System.out.println("id： " + id);

        }

        folder.close(false);// 关闭邮件夹对象
        store.close(); // 关闭连接对象

        return null;
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

    /**
     * 文本解码
     * @param encodeText 解码MimeUtility.encodeText(String text)方法编码后的文本
     * @return 解码后的文本
     * @throws UnsupportedEncodingException
     */
    public static String decodeText(String encodeText) throws UnsupportedEncodingException {
        if (encodeText == null || "".equals(encodeText)) {
            return "";
        } else {
            return MimeUtility.decodeText(encodeText);
        }
    }



}
