package com.example.email.domain;

public class Attachment {

    //该附件所属的邮件id
    private int mailId;
    //该附件路径
    private String attachmentPath;

    public Attachment(int mailId, String attachmentPath){
        this.mailId = mailId;
        this.attachmentPath = attachmentPath;
    }

    public int getMailId() {
        return mailId;
    }

    public void setMailId(int mailId) {
        this.mailId = mailId;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }
}
