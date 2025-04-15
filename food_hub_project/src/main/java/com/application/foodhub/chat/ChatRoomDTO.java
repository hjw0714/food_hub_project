package com.application.foodhub.chat;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChatRoomDTO {

	private Long roomId; // 채팅방 아이디
	private String prtcpUser1; // 참여자1 (USER_ID)
	private String prtcpUser2; // 참여자2 (USER_ID)
	private String status; // 'ACTIVE' 또는 'DELETED'
	private LocalDateTime createdAt; // 생성일
	private LocalDateTime updatedAt; // 업데이트일
	private LocalDateTime deletedAt; // 삭제일 (nullable)

	private String otherUserNickname;
}
