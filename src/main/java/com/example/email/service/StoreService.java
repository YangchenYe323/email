package com.example.email.service;

import com.example.email.domain.Mail;
import com.example.email.mapper.MailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StoreService {

    @Autowired
    private MailMapper mailMapper;
    //private MailDao mailDao;

    public Mail getMail(Long id){
        return null;
        //return mailDao.findMailById(id);
    }

    public int insertMail(Mail mail){
        mailMapper.insert(mail);
        return mail.getId();
    }

}
