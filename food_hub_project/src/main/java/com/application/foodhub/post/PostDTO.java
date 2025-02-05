package com.application.foodhub.post;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class PostDTO {
    private Long postId = null;
    private String userId;
    private String nickname;
    private Long categoryId;
    private Long subCateId;
    private String cateNm;
    private String subCateNm;
    private String title;
    private String content;
    private Long viewCnt;
    private String status;
    @DateTimeFormat(pattern="yyyy-MM-dd-hh-mm")
    private Date createdAt;
    @DateTimeFormat(pattern="yyyy-MM-dd-hh-mm")
    private Date updatedAt;
    
}