package com.application.foodhub.chat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatRoomServiceImpl implements ChatRoomService{

	
	@Autowired
	private ChatDAO chatDAO;
	
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
	    chatDAO.insertRoom(userId1, userId2);
	    return chatDAO.findRoomBetweenUsers(userId1, userId2);
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
}
