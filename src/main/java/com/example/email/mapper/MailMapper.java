package com.example.email.mapper;


import com.example.email.domain.Mail;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

public interface MailMapper {

    @Insert("INSERT INTO mail (u_name, s_name, subject, content, sent_date) VALUES(#{username}, #{sender}, #{subject}, #{content}, #{sentDate})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    int insert(Mail mail);

    @Select("SELECT * FROM mail WHERE u_name = #{userName}")
    List<Mail> getMailByName(String userName);

    /**
     * 查询指定用户的最新已同步邮件收件时间
     * @param userName
     * @return
     */
    @Select("SELECT MAX(sent_date) FROM mail WHERE u_name = #{userName}")
    Date getLastSentDateFor(String userName);

}
