package com.application.foodhub.stats;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class VisitorLogDTO {
	private Long visitorId;
	private String ipAddress;
	private LocalDateTime visitTime;
	private String userId;
}
