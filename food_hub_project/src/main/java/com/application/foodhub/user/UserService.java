package com.application.foodhub.user;

import org.springframework.web.multipart.MultipartFile;

public interface UserService {

	public void register(UserDTO userDTO);
	public String checkValidId (String userId);
	public String checkValidNickname (String nickname);
	public String checkValidEmail (String email);
	public boolean login (UserDTO userDTO);
	
}
