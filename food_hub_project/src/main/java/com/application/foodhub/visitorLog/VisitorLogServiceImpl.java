package com.application.foodhub.visitorLog;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class VisitorLogServiceImpl implements VisitorLogService {

	@Autowired
	private VisitorLogDAO visitorLogDAO;

	@Override
    public void recordVisitor(HttpServletRequest request, String userId) {
        String ipAddress = getClientIp(request);
        LocalDateTime now = LocalDateTime.now();

        boolean shouldRecord = false;

        if (userId != null) {
            // 로그인 사용자 - 마지막 방문 시간 조회
            LocalDateTime lastVisit = visitorLogDAO.getLastVisitTimeTodayByUserId(userId);
            if (lastVisit == null || lastVisit.plusMinutes(10).isBefore(now)) {
                shouldRecord = true;
                System.out.println("[방문 기록 허용 - 로그인] userId: " + userId);
            } else {
                System.out.println("[방문 기록 차단 - 로그인 10분 이내 중복] userId: " + userId);
            }

        } else {
            // 비로그인 사용자 - 마지막 방문 IP 기준 조회
            LocalDateTime lastVisit = visitorLogDAO.getLastVisitTimeTodayByIp(ipAddress);
            if (lastVisit == null || lastVisit.plusMinutes(10).isBefore(now)) {
                shouldRecord = true;
                System.out.println("[방문 기록 허용 - 비로그인] IP: " + ipAddress);
            } else {
                System.out.println("[방문 기록 차단 - 비로그인 10분 이내 중복] IP: " + ipAddress);
            }
        }

        if (shouldRecord) {
            VisitorLogDTO visitorLogDTO = new VisitorLogDTO();
            visitorLogDTO.setIpAddress(ipAddress);
            visitorLogDTO.setUserId(userId);
            visitorLogDTO.setVisitTime(now);
            visitorLogDAO.insertVisitorLog(visitorLogDTO);
            System.out.println("[방문 기록 완료] IP: " + ipAddress + ", USER_ID: " + userId + ", 시간: " + now);
        }
    }

       @Override
       public Long getVisitorCnt(String statDate) {
           Long count = visitorLogDAO.getVisitorCnt(statDate);
           return count != null ? count : 0L;
       }

       @Override
       public Long getTotalVisitorCnt() {
           return visitorLogDAO.getTotalVisitorCnt();
       }

       @Override
       public Long getUserVisitCnt(String userId) {
           return visitorLogDAO.getUserVisitCnt(userId);
       }

       private String getClientIp(HttpServletRequest request) {
           String ip = request.getHeader("X-Forwarded-For");
           if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) ip = request.getRemoteAddr();
           return ip;
       }
   }