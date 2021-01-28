package com.example.email.service;

import com.example.email.domain.Attachment;
import com.example.email.domain.Mail;
import com.example.email.mapper.AttachmentMapper;
import com.example.email.mapper.MailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * 处理存储对象的逻辑
 *
 */
@Service
public class EmailStoreService {
    @Autowired
    MailMapper mailMapper;
    @Autowired
    AttachmentMapper attachmentMapper;

    @Transactional
    public void save(Mail mail){
        int id = mailMapper.insert(mail);
        for (String path: mail.getPaths()){
            Attachment attachment = new Attachment(id, path);
            attachmentMapper.insert(attachment);
        }
    }

}
