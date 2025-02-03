package com.application.foodhub.user;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface UserService {

	public void register(UserDTO userDTO);
	public String checkValidId (String userId);
	public String checkValidNickname (String nickname);
	public String checkValidEmail (String email);
	public boolean login (UserDTO userDTO);
	public UserDTO getUserDetail(String attribute);
	public void updateUser(MultipartFile uploadProfile, UserDTO userDTO) throws IllegalStateException, IOException;
	public void deleteUser(String userId);
	
}
