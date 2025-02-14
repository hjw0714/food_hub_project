package com.application.foodhub.user;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDAO {
	public void register(UserDTO userDTO); 					// 회원가입
	public String checkValidId (String userId); 			// 아이디 중복
	public String checkValidNickname (String nickname); 	// 닉네임 중복
	public String checkValidEmail (String email); 			// 이메일 중복
	public String getEncodedPasswd(String userId); 			// 비밀번호 암호화
	public UserDTO getUserDetail(String userId); 			// 유저 상세정보 
	public void updateUser(UserDTO userDTO); 				// 유저 정보 수정
	public String getDeleteUserProfile(String userId); 		// 유저 정보 삭제
	public void deleteUser(String userId); 					// 유저 탈퇴
	public String findId(Map<String, Object> params); 		// 아이디 찾기
	public String findPasswd(Map<String, Object> params); 	// 비밀번호 찾기
	public void resetPassword(UserDTO userDTO); 			//비밀번호 초기화
	public String findNicknameByUserId(String userId); 		//유저 아이디로 닉네임 찾기
}
