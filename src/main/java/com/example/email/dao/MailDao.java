package com.example.email.dao;

import com.example.email.domain.Mail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

public interface MailDao {

    Mail findMailById(@Param("id") Long id);

    void insertMail(@Param("userName") String userName, @Param("subject") String subject, @Param("senderName") String senderName, @Param("content") String content);

}
