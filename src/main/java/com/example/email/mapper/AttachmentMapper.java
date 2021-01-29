package com.example.email.mapper;

import com.example.email.domain.Attachment;
import org.apache.ibatis.annotations.Insert;

public interface AttachmentMapper {

    @Insert("INSERT INTO attachment_t VALUES(#{mailId}, #{attachmentPath})")
    int insert(Attachment attachment);

}
