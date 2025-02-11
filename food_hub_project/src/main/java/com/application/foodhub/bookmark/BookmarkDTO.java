package com.application.foodhub.bookmark;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class BookmarkDTO {
    private Long bookmarkId;
    private String userId;
    private Long postId;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date createdAt;
    private String title;
}
