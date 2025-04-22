package com.application.foodhub.visitorLog;

import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface VisitorLogDAO {

	// 오늘 날짜의 방문자 통계 조회
	public Long getVisitorCnt(@Param("visitDate") String visitDate);

	boolean existsUserVisitToday(String userId);

	boolean existsIpVisitToday(String ipAddress);

	LocalDateTime getLastVisitTimeTodayByUserId(String userId);

	LocalDateTime getLastVisitTimeTodayByIp(String ipAddress);

	// 방문자 로그 삽입
	public void insertVisitorLog(VisitorLogDTO visitorLogDTO);

	// 사용자별 마지막 방문 시간 조회
	public LocalDateTime getLastVisitTimeByUserId(@Param("userId") String userId);

	// 사용자별 방문 횟수 조회
	public Long getUserVisitCnt(@Param("userId") String userId);

	// 전체 기간 방문자 수 조회
	public Long getTotalVisitorCnt();

}