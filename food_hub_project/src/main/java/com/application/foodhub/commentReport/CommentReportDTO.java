package com.application.foodhub.commentReport;

import java.util.Date;

import lombok.Data;

@Data
public class CommentReportDTO {
    private Long reportId;
    private Long commentId;
    private String userId;
    private String content;
    private String status;
    private Date createdAt;
    private Date updatedAt;
}