package com.application.foodhub.fileUpload;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class FileUploadDTO {
    private Long fileId;
    private Long postId;
    private String fileUUID;
    private String fileName;
    private String filePath;
    @DateTimeFormat(pattern="yyyy-MM-dd-hh-mm")
    private Date uploadDate;
}