package com.example.email.mapper;


import com.example.email.domain.Mail;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

public interface MailMapper {

    @Insert(
            "INSERT INTO mail_t" +
                    "(receiver_name, subject, sender_name, content, receive_date, send_date)" +
                    "VALUES" +
                    "(#{receiverName}, #{subject}, #{senderName}, #{content}, #{receiveDate}, #{sendDate})"
    )
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    int insert(Mail mail);

    /**
     * 查询指定用户的最新已同步邮件收件时间
     * @param userName
     * @return
     */
    @Select("SELECT MAX(receive_date) FROM mail_t WHERE receiver_name = #{userName}")
    Date getLatestReceiveDateFor(String userName);

}
