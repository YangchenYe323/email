package com.example.email.domain;

public class MailWithAttachment {

    private Mail mail;
    private Attachment attachment;

    public Attachment getAttachment() {
        return attachment;
    }

    public Mail getMail() {
        return mail;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }
}
