package com.example.email.mapper;

import com.example.email.domain.UserInfo;
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

    @Update("UPDATE user_info SET password = #{password}, server = #{server}, port = #{port} WHERE u_name = #{username}")
    void update(UserInfo userInfo);

    @Delete("DELETE from user_info WHERE u_name = #{username}")
    void delete(String username);

}
