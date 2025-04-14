package com.application.foodhub.stats;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StatsDAO {
	
	// 오늘 날짜의 방문자 통계 조회
	public Long getVisitorCnt(int categoryId, String statDate);
	
	// 방문자 수 증가
	public void incrementVisitorCnt(int categoryId, String statDate);
	
	// 새로운 통계 기록 삽입
	public void insertVisitorStats(int categoryId, String statDate);
	
	// IP로 방문자 로그 조회
	public VisitorLogDTO getVisitorLogByIp(String ipAdress);
	
	// 방문자 로그 삽입
	public void insertVisitorLog(VisitorLogDTO visitorLogDTO);
	
	// 방문자 로그 업데이트
	public void updateVisitorLog(VisitorLogDTO visitorLogDTO);

}
