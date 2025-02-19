package com.application.foodhub.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.application.foodhub.user.UserDAO;
import com.application.foodhub.user.UserDTO;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class UserDaoTest {
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private UserDTO userDTO;
	
	@BeforeEach
	void setUpTestData() {
		
		userDTO = new UserDTO();
		userDTO.setUserId("yocasu1");
		userDTO.setProfileOriginal("404.png");
		userDTO.setProfileUUID("qwer-asdf-zxcv");
		userDTO.setNickname("yocasu1");
		userDTO.setPasswd(passwordEncoder.encode("1234"));
		userDTO.setEmail("qwerasdf@gmail.com");
		userDTO.setTel("010-1111-1111");
		userDTO.setGender("M");
		userDTO.setBirthday(new Date());
		userDTO.setMembershipType("ADMIN");
		userDTO.setFoundingAt(new Date());
		userDTO.setBusinessType(null);
		userDTO.setSmsYn("N");
		userDTO.setEmailYn("N");
		userDTO.setJoinAt(new Date());
		userDTO.setModifyAt(new Date());
		userDAO.register(userDTO);
	} 
	
	@Test @Order(1) @DisplayName("회원가입 확인")
	void testRegister () {
		userDAO.register(userDTO);
		
	} 
	
	@Test @Order(2) @DisplayName("유저 상세 조회") 
	void testGetuserDetail() {
		UserDTO result = userDAO.getUserDetail(userDTO.getUserId());
		assertThat(result).isNotNull();
		
	}
	
	@Test @Order(3) @DisplayName("비밀번호 암호화") //
	void testPasswdEncoded () {
		String encodedPasswd = userDAO.getEncodedPasswd(userDTO.getUserId());
		assertThat(passwordEncoder.matches("1234", encodedPasswd)).isTrue();
	}
	
	@Test @Order(4) @DisplayName("유저 정보 수정")
	void testUpdateUser() {
		UserDTO before = userDAO.getUserDetail(userDTO.getUserId());
		before.setEmailYn("Y");
		before.setNickname("다나카");
		
		userDAO.updateUser(before);
		
		UserDTO after = userDAO.getUserDetail(userDTO.getUserId());
		assertThat(after)
		.extracting(UserDTO::getEmailYn , UserDTO::getNickname)
		.contains("Y" , "다나카");
	}
	
	@Test @Order(5) @DisplayName("유저 정보 삭제")
	void testDeleteUser() {
		userDAO.deleteUser(userDTO.getUserId());
		UserDTO result = userDAO.getUserDetail(userDTO.getUserId());
		assertThat(result).isNull();
	}


}
