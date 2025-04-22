package com.application.foodhub.visitorLog;

import jakarta.servlet.http.HttpServletRequest;

public interface VisitorLogService {

	public void recordVisitor(HttpServletRequest request, String userId);

	public Long getVisitorCnt(String statDate);

	public Long getTotalVisitorCnt();

	public Long getUserVisitCnt(String userId);

}