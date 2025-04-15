package com.application.foodhub.banner;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BannerDTO {
	
	private Long bannerId;
    private String title;
    private String description;
    private String bannerOriginalName;
    private String bannerUuid;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
	
}
