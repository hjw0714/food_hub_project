package com.application.foodhub.stats;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Service
public class StatsServiceImpl implements StatsService {

    @Autowired
    private StatsDAO statsDAO;

    private static final int VISITOR_CATEGORY_ID = 7; // 방문자 통계용 CATEGORY_ID
    private static final long VISIT_TIMEOUT_MINUTES = 30; // 30분 타임아웃

    @Override
    public void recordVisitor(HttpServletRequest request, String userId) {
        // 클라이언트 IP 추출
        String ipAddress = getClientIp(request);
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 오늘 날짜의 통계가 없으면 새로 삽입
        Long count = statsDAO.getVisitorCnt(VISITOR_CATEGORY_ID, today);
        if (count == null) {
            statsDAO.insertVisitorStats(VISITOR_CATEGORY_ID, today);
        }

        // IP로 기존 방문 로그 조회
        VisitorLogDTO visitorLogDTO = statsDAO.getVisitorLogByIp(ipAddress);
        boolean shouldIncrement = false;

        if (visitorLogDTO == null) {
            // 처음 방문: 로그 삽입 및 카운트 증가
        	visitorLogDTO = new VisitorLogDTO();
        	visitorLogDTO.setIpAddress(ipAddress);
        	visitorLogDTO.setUserId(userId);
            statsDAO.insertVisitorLog(visitorLogDTO);
            shouldIncrement = true;
        } else {
            // 기존 방문 기록이 있는 경우
            LocalDateTime lastVisit = visitorLogDTO.getLastVisit();
            LocalDateTime now = LocalDateTime.now();
            long minutesSinceLastVisit = ChronoUnit.MINUTES.between(lastVisit, now);

            // 30분 경과 여부 확인
            if (minutesSinceLastVisit >= VISIT_TIMEOUT_MINUTES) {
                shouldIncrement = true;
            }

            // 사용자 변경 확인 (로그아웃 후 다른 사용자로 로그인)
            if (userId != null && visitorLogDTO.getUserId() != null && !userId.equals(visitorLogDTO.getUserId())) {
                shouldIncrement = true;
            }

            // 로그 업데이트
            visitorLogDTO.setUserId(userId);
            statsDAO.updateVisitorLog(visitorLogDTO);
        }

        // 카운트 증가
        if (shouldIncrement) {
            statsDAO.incrementVisitorCnt(VISITOR_CATEGORY_ID, today);
        }
    }

    @Override
    public Long getVisitorCnt(String statDate) {
        Long count = statsDAO.getVisitorCnt(VISITOR_CATEGORY_ID, statDate);
        return count != null ? count : 0L;
    }

    // 클라이언트 IP 추출 메서드
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}