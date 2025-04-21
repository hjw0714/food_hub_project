package com.application.foodhub.stats;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StatsDAO {
    // 오늘 날짜의 방문자 통계 조회
    public Long getVisitorCnt(@Param("categoryId") int categoryId, @Param("statDate") String statDate);

    // 방문자 수 증가
    public void incrementVisitorCnt(@Param("categoryId") int categoryId, @Param("statDate") String statDate);

    // 새로운 통계 기록 삽입
    public void insertVisitorStats(@Param("categoryId") int categoryId, @Param("statDate") String statDate);

    // 방문자 로그 삽입
    public void insertVisitorLog(VisitorLogDTO visitorLogDTO);

    // 사용자별 방문 횟수 조회 
    public Long getUserVisitCnt(@Param("userId") String userId);
    
    // 전체 기간 방문자 수 조회
    public Long getTotalVisitorCnt();
    

}