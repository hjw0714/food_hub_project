package com.application.foodhub.comment;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class CommentDTO {
    private Long commentId;
    private Long postId;
    private String userId;
    private Long parentId;
    private String content;
    @DateTimeFormat(pattern="yyyy-MM-dd-hh-mm")
    private Date createdAt;
    @DateTimeFormat(pattern="yyyy-MM-dd-hh-mm")
    private Date updatedAt;
    private String status;
}