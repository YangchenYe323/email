package com.example.email.mapper;

import com.example.email.domain.Attachment;
import org.apache.ibatis.annotations.Insert;

public interface AttachmentMapper {

    @Insert("INSERT INTO attachment VALUES(#{id}, #{path})")
    int insert(Attachment attachment);

}
