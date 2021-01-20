package com.example.email.service;

import com.example.email.domain.Attachment;
import com.example.email.domain.Mail;
import com.example.email.domain.MailLastDate;
import com.example.email.domain.UserInfo;
import com.example.email.mapper.AttachmentMapper;
import com.example.email.mapper.LastDateMapper;
import com.example.email.mapper.MailMapper;
import com.example.email.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreService {

    @Autowired
    private MailMapper mailMapper;
    //private MailDao mailDao;
    @Autowired
    private AttachmentMapper attachmentMapper;
    @Autowired
    private LastDateMapper lastDateMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;

    public Mail getMail(Long id){
        return null;
        //return mailDao.findMailById(id);
    }

    public int insertMail(Mail mail){
        mailMapper.insert(mail);
        return mail.getId();
    }

    public void insertAttachment(Attachment attachment){
        attachmentMapper.insert(attachment);
    }

    public void insertDate(MailLastDate mailLastDate){
        lastDateMapper.insert(mailLastDate);
    }

    public void insertUserInfo(UserInfo userInfo){
        userInfoMapper.insert(userInfo);
    }

    public List<MailLastDate> getAllDate(){
        return lastDateMapper.getAll();
    }

    public void updateDate(MailLastDate mailLastDate){
        lastDateMapper.update(mailLastDate);
    }

    public List<UserInfo> getAllUserInfo(){
        return userInfoMapper.getAll();
    }

}
