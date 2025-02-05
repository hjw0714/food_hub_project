package com.application.foodhub.user;

import java.util.Map;

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
	public String findId(Map<String, Object> params);
	public String findPasswd(Map<String, Object> params);
	public void resetPassword(UserDTO userDTO);
	public String findNicknameByUserId(String userId);
}
