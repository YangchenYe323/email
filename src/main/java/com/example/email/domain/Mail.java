package com.example.email.domain;


import java.util.Date;
import java.util.List;

/**
 *
 */
public class Mail {

    //邮件id
    private int mailId;
    //收件人地址
    private String receiverName;
    //邮件标题
    private String subject;
    //发件人地址
    private String senderName;
    //邮件内容
    private String content;
    //收件时间
    private Date receiveDate;
    //发件时间
    private Date sendDate;
    //附件列表
    List<String> paths;

    public int getMailId() {
        return mailId;
    }

    public void setMailId(int mailId) {
        this.mailId = mailId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getContent() {
        return content;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSubject() {
        return subject;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }
}
