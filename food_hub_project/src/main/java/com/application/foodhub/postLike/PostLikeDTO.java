package com.application.foodhub.postLike;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class PostLikeDTO {
    private Long postId;
    private String userId;
    private String status;
    @DateTimeFormat(pattern="yyyy-MM-dd-hh-mm")
    private Date createdAt;
}