package com.application.foodhub.user;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDAO {
	public void register(UserDTO userDTO);
	public String checkValidId (String userId);
	public String checkValidNickname (String nickname);
	public String checkValidEmail (String email);
	public String getEncodedPasswd(String userId);
	public UserDTO getUserDetail(String userId);
	public void updateUser(UserDTO userDTO);
	public String getDeleteUserProfile(String userId);
	public void deleteUser(String userId);
}
