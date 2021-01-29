package com.example.email.domain;

public class UserInfo {

    //用户id
    private int userId;
    //用户邮箱地址
    private String userName;
    //用户邮箱授权码
    private String password;
    //pop3服务器地址（收件）
    private String popServer;
    //pop3服务器端口
    private int popPort;
    //smtp服务器地址（发件）
    private String smtpServer;
    //smtp服务器地址
    private int smtpPort;

    public String getUserName() {
        return userName;
    }

    public int getUserId() {
        return userId;
    }

    public int getPopPort() {
        return popPort;
    }

    public String getPassword() {
        return password;
    }

    public String getPopServer() {
        return popServer;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPopPort(int popPort) {
        this.popPort = popPort;
    }

    public void setPopServer(String popServer) {
        this.popServer = popServer;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public String getSmtpServer() {
        return smtpServer;
    }

    public void setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
    }

    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }
}
