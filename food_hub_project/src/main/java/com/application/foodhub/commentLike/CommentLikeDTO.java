package com.application.foodhub.commentLike;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class CommentLikeDTO {
    private Long commentId;
    private String userId;
    @DateTimeFormat(pattern="yyyy-MM-dd-hh-mm")
    private Date createdAt;
    private boolean liked; 
    private int likeCount; 
    private boolean success;
}