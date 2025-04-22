package com.application.foodhub.visitorLog;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class VisitorLogDTO {
	
	private Long visitorId;
	private String ipAddress;
	private LocalDateTime visitTime;
	private String userId;
	
}
