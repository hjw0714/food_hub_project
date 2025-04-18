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
	private static final long VISIT_TIMEOUT_MINUTES = 1; // 테스트 타임아웃 (1분) -> 추후 수정

	@Override
	public void recordVisitor(HttpServletRequest request, String userId) {
		HttpSession session = request.getSession();

		// 클라이언트 IP 추출
		String ipAddress = getClientIp(request);
		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		// 오늘 통계 없으면 삽입
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

		// IP 기반 방문 기록 키
		String sessionIpKey = "lastVisitIp_" + ipAddress;
		String sessionUserKey = userId != null ? "lastVisitUser_" + userId : null;

		// 현재 시간
		LocalDateTime now = LocalDateTime.now();

		if (userId == null) {
			// 비로그인 상태
			LocalDateTime lastVisit = (LocalDateTime) session.getAttribute(sessionIpKey);

			if (lastVisit == null) {
				// 최초 방문
				shouldIncrement = true;
				session.setAttribute(sessionIpKey, now);
				System.out.println("[비로그인 최초 방문] IP: " + ipAddress + ", 방문자 카운트 증가");
			} else {
				// 이전 방문 있음: 타임아웃 체크
				long timeSinceLastVisit = ChronoUnit.MINUTES.between(lastVisit, now);
				if (timeSinceLastVisit >= VISIT_TIMEOUT_MINUTES) {
					shouldIncrement = true;
					session.setAttribute(sessionIpKey, now);
					System.out.println("[비로그인 재방문] IP: " + ipAddress + ", 타임아웃 경과, 방문자 카운트 증가");
				} else {
					System.out.println("[비로그인 재방문] IP: " + ipAddress + ", 타임아웃 미경과, 방문자 카운트 증가 안 함");
				}
			}
		} else {
			// 로그인 상태
			LocalDateTime lastIpVisit = (LocalDateTime) session.getAttribute(sessionIpKey);
			Boolean hasVisited = (Boolean) session.getAttribute(sessionUserKey);
			String lastLoggedOutUserId = (String) session.getAttribute("lastLoggedOutUserId");
			LocalDateTime lastLogoutTime = (LocalDateTime) session.getAttribute("lastLogoutTime");

			System.out.println("[로그인 상태] 사용자: " + userId + ", lastIpVisit: " + lastIpVisit + ", lastLoggedOutUserId: "
					+ lastLoggedOutUserId + ", hasVisited: " + hasVisited + ", lastLogoutTime: " + lastLogoutTime);

			// 첫 로그인 시 비로그인 방문 후 타임아웃 내인지 체크
			if (lastIpVisit != null && (hasVisited == null || !hasVisited)) {
				long timeSinceLastIpVisit = ChronoUnit.MINUTES.between(lastIpVisit, now);
				if (timeSinceLastIpVisit < VISIT_TIMEOUT_MINUTES) {
					shouldIncrement = false;
					session.setAttribute(sessionUserKey, true);
					
					System.out.println("[로그인] 사용자: " + userId + ", 비로그인 방문 후 타임아웃 미경과, 방문자 카운트 증가 안 함");
				}
			}

			// 동일 사용자 재로그인 체크
			if (shouldIncrement && lastLoggedOutUserId != null && lastLoggedOutUserId.equals(userId) && lastLogoutTime != null) {
				long timeSinceLastLogout = ChronoUnit.MINUTES.between(lastLogoutTime, now);
				if (timeSinceLastLogout < VISIT_TIMEOUT_MINUTES) {
					shouldIncrement = false;
					session.setAttribute(sessionUserKey, true);
					
					System.out.println("[로그인] 사용자: " + userId + ", 로그아웃 후 타임아웃 미경과, 방문자 카운트 증가 안 함");
				} else {
					shouldIncrement = true;
					session.setAttribute(sessionUserKey, true);
					
					System.out.println("[로그인] 사용자: " + userId + ", 로그아웃 후 타임아웃 경과, 방문자 카운트 증가");
				}
			}
			
			// 이미 방문한 사용자 체크
			if (shouldIncrement && hasVisited != null && hasVisited) {
				shouldIncrement = false;
				
				System.out.println("[로그인] 사용자: " + userId + ", 이미 방문한 사용자, 방문자 카운트 증가 안 함");
			}
			
			// 최초 로그인 또는 다른 사용자
			if (shouldIncrement && (hasVisited == null || !hasVisited)) {
				shouldIncrement = true;
				session.setAttribute(sessionUserKey, true);
				
				System.out.println("[로그인] 사용자: " + userId + ", 최초 로그인 또는 다른 사용자, 방문자 카운트 증가");
			}
		}

		// 조건 충족 시 방문 로그 삽입 및 카운트 증가
		if (shouldIncrement) {
			statsDAO.insertVisitorLog(visitorLogDTO);
			statsDAO.incrementVisitorCnt(VISITOR_CATEGORY_ID, today);
			System.out.println(
					"[카운트 증가] IP: " + ipAddress + ", 사용자: " + (userId != null ? userId : "없음") + ", 방문자 카운트 증가 완료");
		} else {
			System.out.println(
					"[카운트 미증가] IP: " + ipAddress + ", 사용자: " + (userId != null ? userId : "없음") + ", 방문자 카운트 증가 안 함");
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
