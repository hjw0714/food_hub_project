package com.application.foodhub.postReport;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class PostReportDTO {
    private Long postId;
    private String userId;
    private String content;
    private String status;
    @DateTimeFormat(pattern="yyyy-MM-dd-hh-mm")
    private Date createdAt;
    @DateTimeFormat(pattern="yyyy-MM-dd-hh-mm")
    private Date updatedAt;
}