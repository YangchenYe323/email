package com.example.email.mapper;

import com.example.email.domain.UserInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserInfoMapper {

    @Select("SELECT * FROM user_info")
    @Results({
            @Result(column = "user_name", property = "userName"),
            @Result(column = "pop_server", property = "popServer"),
            @Result(column = "pop_port", property = "popPort"),
            @Result(column = "smtp_server", property = "smtpServer"),
            @Result(column = "smtp_port", property = "smtpPort")
        }
    )
    List<UserInfo> getAll();

    @Insert(
            "INSERT INTO user_info SET " +
                    "user_name = #{userName}, " +
                    "password = #{password}, " +
                    "pop_server = #{popServer}, " +
                    "pop_port = #{popPort}, " +
                    "smtp_server = #{smtpServer}, " +
                    "smtp_port = #{smtpPort}"
    )
    int insert(UserInfo userInfo);

}
