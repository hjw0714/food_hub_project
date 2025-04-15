package com.application.foodhub.stats;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class VisitorLogDTO {
	private Long visitorId;
	private String ipAddress;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime lastVisit;
	private String userId;
}
