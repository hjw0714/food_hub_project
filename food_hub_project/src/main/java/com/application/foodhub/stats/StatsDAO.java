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
    
    // 오늘 날짜 게시글 신고 통계 조회
    public Long getPostReportCnt(@Param("categoryId") int categoryId, @Param("statDate") String statDate);
    
    // 새로운 통계 기록 삽입
    public void insertPostReport(@Param("categoryId") int categoryId, @Param("statDate") String statDate);
    
    // 게시글 신고 수 증가
    public void increasePostReportCnt(@Param("categoryId") int categoryId, @Param("statDate") String statDate);
    
    // 오늘 날짜 댓글 신고 통계 조회
    public Long getCommentReportCnt(@Param("categoryId") int categoryId, @Param("statDate") String statDate);
    
    // 새로운 통계 기록 삽입
    public void insertCommentReport(@Param("categoryId") int categoryId, @Param("statDate") String statDate);
    
    // 댓글 신고 수 증가
    public void increaseCommentReportCnt(@Param("categoryId") int categoryId, @Param("statDate") String statDate);
    
    // 오늘 날짜 게시글 통계 조회
    public Long getTotalPostCnt(@Param("categoryId") int categoryId, @Param("statDate") String statDate);
    
    // 새로운 게시글 통계 기록 삽입
    public void insertTotalPost(@Param("categoryId") int categoryId, @Param("statDate") String statDate);
    
    // 게시글 수 증가
    public void increaseTotalPostCnt(@Param("categoryId") int categoryId, @Param("statDate") String statDate);
    
    // 오늘 날짜 카테고리별 게시글 통계 조회
    public Long getCategoryPostCnt(@Param("categoryId") int categoryId, @Param("statDate") String statDate);
    
    // 새로운 카테고리별 게시글 통계 기록 삽입
    public void insertCategoryPost(@Param("categoryId") int categoryId, @Param("statDate") String statDate);
    
    // 카테고리별 게시글 수 증가
    public void increaseCategoryPostCnt(@Param("categoryId") int categoryId, @Param("statDate") String statDate);
    
    // 오늘 날짜 댓글 통계 조회
    public Long getCommentCnt(@Param("categoryId") int categoryId, @Param("statDate") String statDate);
    
    // 새로운 댓글 통계 기록 삽입
    public void insertComment(@Param("categoryId") int categoryId, @Param("statDate") String statDate);
    
    // 댓글 수 증가
    public void increaseCommentCnt(@Param("categoryId") int categoryId, @Param("statDate") String statDate);
    
    // 총회원수 조회
    public Long getAllTotalCount(@Param("categoryId") int categoryId);
    
    // 오늘 날짜 총회원 통계 조회
    public Long getTotalUserCnt(@Param("categoryId") int categoryId, @Param("statDate") String statDate);
    
    // 새로운 총회원 통계 기록 삽입
    public void insertTotalUser(@Param("categoryId") int categoryId, @Param("statDate") String statDate, @Param("allTotalCount") Long allTotalCount);
    
    // 총회원 수 증가
    public void increaseTotalUserCnt(@Param("categoryId") int categoryId, @Param("statDate") String statDate);
    
    // 오늘 날짜 신규가입 통계 조회
    public Long getJoinUserCnt(@Param("categoryId") int categoryId, @Param("statDate") String statDate);
    
    // 새로운 신규가입 통계 기록 삽입
    public void insertJoinUser(@Param("categoryId") int categoryId, @Param("statDate") String statDate);
    
    // 신규가입 수 증가
    public void increaseJoinUserCnt(@Param("categoryId") int categoryId, @Param("statDate") String statDate);
    
    // 오늘 날짜 탈퇴 통계 조회
    public Long getDeleteUserCnt(@Param("categoryId") int categoryId, @Param("statDate") String statDate);
    
    // 새로운 탈퇴 통계 기록 삽입
    public void insertDeleteUser(@Param("categoryId") int categoryId, @Param("statDate") String statDate);
    
    // 탈퇴 수 증가
    public void increaseDeleteUserCnt(@Param("categoryId") int categoryId, @Param("statDate") String statDate);
    
    // 탈퇴 시 총회원수 감소
    public void decreaseTotalUser(@Param("categoryId") int categoryId, @Param("statDate") String statDate);

}