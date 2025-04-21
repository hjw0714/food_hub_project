package com.application.foodhub.stats;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Service
@Transactional
public class StatsServiceImpl implements StatsService {

    @Autowired
    private StatsDAO statsDAO;

    private static final int VISITOR_CATEGORY_ID = 14; // 방문자 통계용 CATEGORY_ID
    private static final long VISIT_TIMEOUT_MINUTES = 1; // 테스트 타임아웃 (1분)

    @Override
    public void recordVisitor(HttpServletRequest request, String userId) {
        HttpSession session = request.getSession();

        // 클라이언트 IP 추출
        String ipAddress = getClientIp(request);
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 오늘 통계 없으면 삽입 (카운트는 0으로 초기화)
        Long count = statsDAO.getVisitorCnt(VISITOR_CATEGORY_ID, today);
        System.out.println("[통계 조회] 날짜: " + today + ", 카운트: " + (count != null ? count : "null"));

        if (count == null) {
            statsDAO.insertVisitorStats(VISITOR_CATEGORY_ID, today);
            System.out.println("[통계 초기화] " + today + " 방문자 통계 생성");
        }

        boolean shouldIncrement = false;
        VisitorLogDTO visitorLogDTO = new VisitorLogDTO();
        visitorLogDTO.setIpAddress(ipAddress);
        visitorLogDTO.setUserId(userId);
        visitorLogDTO.setVisitTime(LocalDateTime.now()); // 방문 시간 명시적 설정

        // IP 기반 방문 기록 키
        String sessionIpKey = "lastVisitIp_" + ipAddress;
        String sessionUserKey = "lastUserId_" + ipAddress; // 이전 사용자 ID 저장 키
        // 현재 시간
        LocalDateTime now = LocalDateTime.now();

        if (userId == null) {
            // 비로그인 상태
            LocalDateTime lastVisit = (LocalDateTime) session.getAttribute(sessionIpKey);

            if (lastVisit == null) {
                // 최초 방문
                shouldIncrement = true;
                session.setAttribute(sessionIpKey, now);
                System.out.println("[비로그인 최초 방문] IP: " + ipAddress + ", 세션 ID: " + session.getId() + ", 카운트 증가");
            } else {
                // 재방문: 타임아웃 체크
                long timeSinceLastVisit = ChronoUnit.MINUTES.between(lastVisit, now);
                if (timeSinceLastVisit >= VISIT_TIMEOUT_MINUTES) {
                    shouldIncrement = true;
                    session.setAttribute(sessionIpKey, now);
                    System.out.println("[비로그인 재방문] IP: " + ipAddress + ", 세션 ID: " + session.getId()
                            + ", 타임아웃 경과 (" + timeSinceLastVisit + "분), 카운트 증가");
                } else {
                    System.out.println("[비로그인 재방문] IP: " + ipAddress + ", 세션 ID: " + session.getId()
                            + ", 타임아웃 미경과 (" + timeSinceLastVisit + "분), 카운트 증가 안 함");
                }
            }
            // 비로그인 상태에서는 사용자 ID 제거
            session.removeAttribute(sessionUserKey);
        } else {
            // 로그인 상태
            LocalDateTime lastIpVisit = (LocalDateTime) session.getAttribute(sessionIpKey);
            String lastUserId = (String) session.getAttribute(sessionUserKey);
            LocalDateTime lastUserVisit = statsDAO.getLastVisitTimeByUserId(userId);

            System.out.println("[로그인 상태] 사용자: " + userId + ", 세션 ID: " + session.getId()
                    + ", lastIpVisit: " + lastIpVisit + ", lastUserId: " + lastUserId + ", lastUserVisit: " + lastUserVisit);

            if (lastIpVisit != null) {
                // 비로그인 방문 기록 있음: 타임아웃 체크
                long timeSinceLastIpVisit = ChronoUnit.MINUTES.between(lastIpVisit, now);
                if (timeSinceLastIpVisit < VISIT_TIMEOUT_MINUTES) {
                    // 비로그인 방문 후 타임아웃 미경과: 카운트 증가 안 함
                    System.out.println("[로그인] 사용자: " + userId + ", 비로그인 방문 후 로그인, "
                            + "타임아웃 미경과 (" + timeSinceLastIpVisit + "분), 카운트 증가 안 함");
                } else {
                    // 비로그인 방문 후 타임아웃 경과: 사용자 방문 기록 확인
                    if (lastUserId != null && lastUserId.equals(userId) && lastUserVisit != null) {
                        long timeSinceLastUserVisit = ChronoUnit.MINUTES.between(lastUserVisit, now);
                        if (timeSinceLastUserVisit < VISIT_TIMEOUT_MINUTES) {
                            // 동일 사용자, 이전 로그인 방문이 타임아웃 미경과: 카운트 증가 안 함
                            System.out.println("[로그인] 사용자: " + userId + ", 이전 로그인 방문 후 타임아웃 미경과 ("
                                    + timeSinceLastUserVisit + "분), 카운트 증가 안 함");
                        } else {
                            // 동일 사용자, 이전 로그인 방문이 타임아웃 경과: 카운트 증가
                            shouldIncrement = true;
                            System.out.println("[로그인] 사용자: " + userId + ", 이전 로그인 방문 후 타임아웃 경과 ("
                                    + timeSinceLastUserVisit + "분), 카운트 증가");
                        }
                    } else {
                        // 다른 사용자 또는 사용자 최초 로그인: 카운트 증가
                        shouldIncrement = true;
                        System.out.println("[로그인] 사용자: " + userId + ", 비로그인 방문 후 첫 로그인 (다른 사용자 또는 최초), "
                                + "타임아웃 경과 (" + timeSinceLastIpVisit + "분), 카운트 증가");
                    }
                }
            } else {
                // 비로그인 방문 기록 없음: 사용자 방문 기록 확인
                if (lastUserVisit != null) {
                    long timeSinceLastUserVisit = ChronoUnit.MINUTES.between(lastUserVisit, now);
                    if (timeSinceLastUserVisit < VISIT_TIMEOUT_MINUTES) {
                        // 사용자 이전 방문이 타임아웃 미경과: 카운트 증가 안 함
                        System.out.println("[로그인] 사용자: " + userId + ", 이전 로그인 방문 후 타임아웃 미경과 ("
                                + timeSinceLastUserVisit + "분), 카운트 증가 안 함");
                    } else {
                        // 사용자 이전 방문이 타임아웃 경과: 카운트 증가
                        shouldIncrement = true;
                        System.out.println("[로그인] 사용자: " + userId + ", 이전 로그인 방문 후 타임아웃 경과 ("
                                + timeSinceLastUserVisit + "분), 카운트 증가");
                    }
                } else {
                    // 사용자 최초 로그인: 카운트 증가
                    shouldIncrement = true;
                    System.out.println("[로그인] 사용자: " + userId + ", 최초 로그인 (비로그인 방문 기록 없음), 카운트 증가");
                }
            }

            // IP 방문 시간 및 사용자 ID 업데이트
            session.setAttribute(sessionIpKey, now);
            session.setAttribute(sessionUserKey, userId);
        }

        // 조건 충족 시 방문 로그 삽입 및 카운트 증가
        if (shouldIncrement) {
            statsDAO.insertVisitorLog(visitorLogDTO);
            statsDAO.incrementVisitorCnt(VISITOR_CATEGORY_ID, today);
            Long updatedCount = statsDAO.getVisitorCnt(VISITOR_CATEGORY_ID, today);
            System.out.println("[카운트 증가] IP: " + ipAddress + ", 사용자: " + (userId != null ? userId : "없음")
                    + ", 세션 ID: " + session.getId() + ", 방문자 카운트 증가 완료, 업데이트 후 카운트: " + updatedCount);
        } else {
            System.out.println("[카운트 미증가] IP: " + ipAddress + ", 사용자: " + (userId != null ? userId : "없음")
                    + ", 세션 ID: " + session.getId() + ", 방문자 카운트 증가 안 함");
        }
    }

    @Override
    public Long getVisitorCnt(String statDate) {
        Long count = statsDAO.getVisitorCnt(VISITOR_CATEGORY_ID, statDate);
        return count != null ? count : 0L;
    }

    @Override
    public Long getTotalVisitorCnt() {
        return statsDAO.getTotalVisitorCnt();
    }

    @Override
    public Long getUserVisitCnt(String userId) {
        return statsDAO.getUserVisitCnt(userId);
    }

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