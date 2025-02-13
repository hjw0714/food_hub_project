package com.application.foodhub.commentReport;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class CommentReportDTO {
    private Long reportId;
    private Long commentId;
    private String userId;
    private String content;
    private String status;
    @DateTimeFormat(pattern="yyyy-MM-dd-hh-mm")
    private Date createdAt;
    @DateTimeFormat(pattern="yyyy-MM-dd-hh-mm")
    private Date updatedAt;
}