package com.example.email.domain;

import java.util.List;

public class MailWithAttachment {

    private Mail mail;
    private List<Attachment> attachments;

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public Mail getMail() {
        return mail;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }
}
