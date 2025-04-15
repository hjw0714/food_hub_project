package com.application.foodhub.chat;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ChatDAO {
	
	public List<ChatRoomDTO> findPrivateRoomsByUser(@Param("userId") String userId);
	
	public  ChatRoomDTO findRoomBetweenUsers(@Param("userId1") String userId1, @Param("userId2") String userId2);

    public void insertRoom(@Param("userId1") String userId1, @Param("userId2") String userId2);

	public void insertChatMessage(ChatMessageDTO messageDTO);

	public List<ChatMessageDTO> getMessagesByRoomId(Long roomId);

	public ChatRoomDTO findRoomById(Long roomId);
	

}