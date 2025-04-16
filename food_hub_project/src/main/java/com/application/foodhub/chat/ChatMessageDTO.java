package com.application.foodhub.chat;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class ChatMessageDTO {

	private Long messageId;
    private Long roomId;
    private String senderId;
    private String receiveId;
    private String chatContent;
    private Timestamp createdAt;
    
    private String sender;
}
