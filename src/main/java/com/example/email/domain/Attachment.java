package com.example.email.domain;

import java.util.List;

public class Attachment {

    private int id;
    private List<String> paths;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }
}
