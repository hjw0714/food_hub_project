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

    private static final int VISITOR_CATEGORY_ID = 7;     // 방문자 통계용 CATEGORY_ID
    private static final long VISIT_TIMEOUT_MINUTES = 1;  // 테스트 타임아웃 (1분) -> 추후 수정

    @Override
    public void recordVisitor(HttpServletRequest request, String userId) {
       HttpSession session = request.getSession();
       
        // 클라이언트 IP 추출
        String ipAddress = getClientIp(request);
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 오늘 통계 없으면 삽입
        Long count = statsDAO.getVisitorCnt(VISITOR_CATEGORY_ID, today);
        if (count == null) {
            statsDAO.insertVisitorStats(VISITOR_CATEGORY_ID, today);
        }
        
        // 로그아웃 요청 체크
        if ("/foodhub/user/logout".equals(request.getRequestURI())) {
         //로그아웃 시 카운트 증가 안함, 로그아웃 시간 저장
           session.setAttribute("lastLogoutTime", LocalDateTime.now());
           // 이전 사용자 정보 저장
           session.setAttribute("lastLoggedOutUserId", userId);
           // 로그아웃 직후 플래그
           session.setAttribute("justLoggedOut", true);
           
           System.out.println("Logout request detected for user: " + userId + ", no count increment");
           
           return;  // 로그아웃 시 카운트 증가 안 함
      }
        
        // 로그아웃 직후 리다이렉트 처리
        Boolean justLoggedOut = (Boolean) session.getAttribute("justLoggedOut");
        if (justLoggedOut != null && justLoggedOut && userId == null) {
           session.removeAttribute("justLoggedOut");  // 플래그 제거
           return;  // 로그아웃 후 /foodhub 리다이렉트로 인한 요청은 카운트 증가 안 함
      }
        
        boolean shouldIncrement = false;
        VisitorLogDTO visitorLogDTO = new VisitorLogDTO();
        visitorLogDTO.setIpAddress(ipAddress);
        visitorLogDTO.setUserId(userId);
        
        // IP 기반 방문 기록 키
        String sessionIpKey = "lastVisitIp_" + ipAddress;
        
        if (userId == null) {
           // 비로그인 상태
           LocalDateTime lastVisit = (LocalDateTime) session.getAttribute(sessionIpKey);
           
           if (lastVisit == null) {
              // 최초 방문: 로그 기록 및 카운트 증가
              shouldIncrement = true;
              session.setAttribute(sessionIpKey, LocalDateTime.now());
              
              System.out.println("Non-logged-in first visit from IP: " + ipAddress);
              
         }
           else {
              // 이전 방문 있음: 타임아웃 체크
              long timeSinceLastVisit = ChronoUnit.MINUTES.between(lastVisit, LocalDateTime.now());
              if (timeSinceLastVisit >= VISIT_TIMEOUT_MINUTES) {
                shouldIncrement = true;   // 지금시간 - 마지막방문시간 이 VISIT_TIMEOUT_MINUTES 보다 크or같이면 방문자 +1 
                session.setAttribute(sessionIpKey, LocalDateTime.now());
                
                System.out.println("Non-logged-in visit after timeout from IP: " + ipAddress);
                
             }
           }
         
      }
        else {
           // 로그인 상태
           String sessionUserKey = "lastVisitUser_" + userId;
           Boolean hasVisited = (Boolean) session.getAttribute(sessionUserKey);
           
           // IP 기반 이전 방문 기록 확인 (도메인 쩝속 후 로그인 시 중복 카운트 방지)
           LocalDateTime lastIpVisit = (LocalDateTime) session.getAttribute(sessionIpKey);
           if (lastIpVisit != null) {
            long timeSinceLastIpVisit = ChronoUnit.MINUTES.between(lastIpVisit, LocalDateTime.now());
            if (timeSinceLastIpVisit < VISIT_TIMEOUT_MINUTES) {
               // 비로그인 상태에서 최근 방문 기록이 있으면 카운트 증가 안 함
               shouldIncrement = false;
               
               System.out.println("Logged-in user " + userId + " visited recently as non-logged-in, no count increment");
               
            }
         }
           
           // 로그아웃 후 재로그인 시 타음아웃 체크
           LocalDateTime lastLogoutTime = (LocalDateTime) session.getAttribute("lastLogoutTime");
           String lastLoggedOutUserId = (String) session.getAttribute("lastLoggedOutUserId");
           
           if (lastLogoutTime != null && lastLoggedOutUserId != null) {
            long timeSinceLastLogout = ChronoUnit.MINUTES.between(lastLogoutTime, LocalDateTime.now());
            if (lastLoggedOutUserId.equals(userId)) {
               // 동일 유저가 로그아웃 후 재로그인
               if (timeSinceLastLogout < VISIT_TIMEOUT_MINUTES) {
                  // VISIT_TIMEOUT_MINUTES 시간 내 재로그인 시 카운트 증가 안 함
                  shouldIncrement = false;
                  
                  System.out.println("Same user " + userId + " re-login within timeout, no count increment");
                  
               }
               else {
                  // VISIT_TIMEOUT_MINUTES 시간 경과 시 카운트 증가
                  shouldIncrement = true;
                  
                  System.out.println("Same user " + userId + " re-login after timeout, count increment");
                  
               }
               
            }
            else {
               // 다른 유저는 VISIT_TIMEOUT_MINUTES 시간 상관없이 카운트 증가
               shouldIncrement = true;
               
               System.out.println("Different user " + userId + " login after logout of " + lastLoggedOutUserId + ", count increment");
               
            }
         }
           
           else if (hasVisited == null || !hasVisited) {
                // 첫 로그인 또는 로그아웃 기록 없음
                shouldIncrement = true;
                session.setAttribute(sessionUserKey, true);
                
                System.out.println("First login for user: " + userId + ", count increment");
                
            }
        }

        // 조건 충족 시 방문 로그 삽입 및 카운트 증가 
        if (shouldIncrement) {
           statsDAO.insertVisitorLog(visitorLogDTO);
         statsDAO.incrementVisitorCnt(VISITOR_CATEGORY_ID, today);
         
         System.out.println("Visitor count incremented for IP: " + ipAddress + ", User: " + userId);
         
      }
    }

    @Override
    public Long getVisitorCnt(String statDate) {
        Long count = statsDAO.getVisitorCnt(VISITOR_CATEGORY_ID, statDate);
        return count != null ? count : 0L;
    }

    public Long getTotalVisitorCnt() {
        return statsDAO.getTotalVisitorCnt();
    }

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
