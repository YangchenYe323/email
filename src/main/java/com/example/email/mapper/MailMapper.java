package com.example.email.mapper;


import com.example.email.domain.Mail;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

public interface MailMapper {

    @Insert("INSERT INTO mail (u_name, s_name, subject, content) VALUES(#{username}, #{sender}, #{subject}, #{content})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    int insert(Mail mail);

}
