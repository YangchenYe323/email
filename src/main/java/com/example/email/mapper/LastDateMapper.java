package com.example.email.mapper;

import com.example.email.domain.MailLastDate;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface LastDateMapper {

    @Insert("INSERT INTO last_date VALUES (#{id}, #{date})")
    void insert(MailLastDate mailLastDate);

    @Update("UPDATE last_date SET date = #{date} WHERE id = #{id}")
    void update(MailLastDate mailLastDate);

    @Select("SELECT * FROM last_date")
    List<MailLastDate> getAll();
}
