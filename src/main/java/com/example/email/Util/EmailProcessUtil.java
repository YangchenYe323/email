package com.example.email.Util;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * 处理javamail API中message对象的一些方法
 */
public class EmailProcessUtil {

    /**
     * 获取邮件体正文并存储在StringBuffer中
     * 邮件体结构: Part{multipart} -> [Part{text/plain}, Part{multipart} -> [Part[text]]]
     * 递归扫描part，获取类型为text/plain的内容
     * @param part 邮件体对象
     * @param content 正文存储缓存
     * @throws MessagingException
     * @throws IOException
     */
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

    /**
     * 判断邮件体是否包含附件
     * 递归扫描邮件体part，寻找disposition为Part.ATTACHMENT或Part.INLINE的邮件体，此为附件邮件体
     * @param part 邮件体对象
     * @return
     * @throws MessagingException
     * @throws IOException
     */
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

    /**
     * 将邮件体附件保存到指定目录，并将生成的文件名压入列表
     * @param part 邮件体对象
     * @param destDir 指定目录
     * @param attachmentPaths 文件名列表
     * @throws UnsupportedEncodingException
     * @throws MessagingException
     * @throws FileNotFoundException
     * @throws IOException
     */
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
                    if (contentType.contains("name") || contentType.contains("application")) {
                        saveFile(bodyPart.getInputStream(), destDir, decodeText(bodyPart.getFileName()));
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            saveAttachment((Part) part.getContent(),destDir, attachmentPaths);
        }
    }

    /**
     * 保存文件帮助方法
     * @param is 需要保存的输入流
     * @param destDir 目标目录
     * @param fileName 文件名字
     * @throws IOException
     */
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

    /**
     * 返回邮件的收件时间
     * @param msg 邮件对象
     * @return
     */
    public static Date getSentDate(Message msg) throws MessagingException {
        return msg.getSentDate();
    }

}
