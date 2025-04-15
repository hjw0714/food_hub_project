package com.application.foodhub.chat;

import java.util.List;

public interface ChatRoomService {

	public List<ChatRoomDTO> getMyPrivateRooms(String userId);

	public ChatRoomDTO findRoomBetweenUsers(String userId, String userId2);

	public ChatRoomDTO createPrivateRoom(String userId, String userId2);
	
	public void saveMessage(ChatMessageDTO messageDTO);
	public List<ChatMessageDTO> getMessagesByRoomId(Long roomId);

	public String findOtherUserId(Long roomIdLong, String senderId);
	
	public List<ChatMessageDTO> getChatMessages(Long roomId);

}
