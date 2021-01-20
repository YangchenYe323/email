package com.example.email.mapper;

import com.example.email.domain.UserInfo;
import org.apache.catalina.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserInfoMapper {

    @Select("SELECT * FROM user_info")
    @Results({
            @Result(column = "u_name", property = "username"),
    }
    )
    List<UserInfo> getAll();

    @Insert("INSERT INTO user_info (u_name, password, server, port) VALUES (#{username}, #{password}, #{server}, #{port})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    int insert(UserInfo userInfo);

}
