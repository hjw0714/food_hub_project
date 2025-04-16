package com.application.foodhub.stats;

import jakarta.servlet.http.HttpServletRequest;

public interface StatsService {
	
	public void recordVisitor(HttpServletRequest request, String userId);
	public Long getVisitorCnt(String statDate);
	public Long getTotalVisitorCnt();
    public Long getUserVisitCnt(String userId);

}
