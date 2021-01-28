package com.example.email.domain;


import java.util.Date;
import java.util.List;

/**
 *
 */
public class Mail {

    //邮件id
    private int id;
    //收件人地址
    private String username;
    //邮件标题
    private String subject;
    //发件人地址
    private String sender;
    //邮件内容
    private String content;
    //收件时间
    private Date sentDate;
    //附件列表
    List<String> paths;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public String getSender() {
        return sender;
    }

    public String getSubject() {
        return subject;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }
}
