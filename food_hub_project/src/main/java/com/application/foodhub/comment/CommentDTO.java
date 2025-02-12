package com.application.foodhub.comment;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class CommentDTO {
    private Long commentId;
    private Long postId;
    private String userId;
    private String nickname;
    private Long parentId;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private Date updatedAt;
    private String status;
    private String profileUUID;
    
    // ✅ 추가된 필드
    private int likeCount;  // 추천 수
    private boolean likedByUser; // 로그인한 유저가 추천했는지 여부
}