package com.application.foodhub.chat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
public class ChatRoomServiceImpl implements ChatRoomService{

	
	@Autowired
	private ChatDAO chatDAO;
	
	@Autowired
	private SimpMessageSendingOperations messagingTemplate;
	
	 @Override
	    public List<ChatRoomDTO> getMyPrivateRooms(String userId) {
	        return chatDAO.findPrivateRoomsByUser(userId);
	    }

	
	@Override
	public ChatRoomDTO findRoomBetweenUsers(String userId1, String userId2) {
	    return chatDAO.findRoomBetweenUsers(userId1, userId2);
	}

	@Override
	public ChatRoomDTO createPrivateRoom(String userId1, String userId2) {
	    // ✅ ACTIVE 상태의 방이 있는지 먼저 확인
	    ChatRoomDTO existingActiveRoom = chatDAO.findRoomBetweenUsers(userId1, userId2);
	    if (existingActiveRoom != null) {
	        throw new IllegalStateException("이미 존재하는 활성화된 채팅방이 있습니다.");
	    }

	    // ✅ 없다면 새로운 방 생성
	    chatDAO.insertRoom(userId1, userId2);
	    return chatDAO.findRoomBetweenUsers(userId1, userId2);  // 방금 만든 방 조회
	}

	
	@Override
    public void saveMessage(ChatMessageDTO messageDTO) {
        chatDAO.insertChatMessage(messageDTO);
    }

    @Override
    public List<ChatMessageDTO> getMessagesByRoomId(Long roomId) {
        return chatDAO.getMessagesByRoomId(roomId);
    }


    @Override
    public String findOtherUserId(Long roomId, String myUserId) {
        ChatRoomDTO room = chatDAO.findRoomById(roomId);
        if (room == null) return null;

        if (room.getPrtcpUser1().equals(myUserId)) {
            return room.getPrtcpUser2();
        } else if (room.getPrtcpUser2().equals(myUserId)) {
            return room.getPrtcpUser1();
        }
        return null;
    }
    
    @Override
    public List<ChatMessageDTO> getChatMessages(Long roomId) {
        return chatDAO.getMessagesByRoomId(roomId);
    }
    
    @Override
    public void deleteChatRoomForUser(Long roomId, String userId) {
        ChatRoomDTO room = chatDAO.findRoomById(roomId);

        if (room == null || (!room.getPrtcpUser1().equals(userId) && !room.getPrtcpUser2().equals(userId))) {
            throw new IllegalArgumentException("대화방 정보가 일치하지 않음");
        }

        chatDAO.updateChatRoomStatusToDeleted(roomId);

        // 상대방에게 '나가셨습니다' 메시지 전송
        ChatMessage leaveMsg = ChatMessage.builder()
                .sender(userId)
                .type(MessageType.LEAVE)
                .build();

        messagingTemplate.convertAndSend("/topic/private." + roomId, leaveMsg);
    }
}
