package com.example.email.domain;

public class UserInfo {

    private int id;
    private String username;
    private String password;
    private String server;
    private int port;

    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }

    public int getPort() {
        return port;
    }

    public String getPassword() {
        return password;
    }

    public String getServer() {
        return server;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setServer(String server) {
        this.server = server;
    }
}
