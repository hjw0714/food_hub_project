package com.application.foodhub.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.Times;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {

	@Mock 
	private UserDAO userDAO;
	
	@Mock
	private PasswordEncoder passwordEncoder;
	
	@InjectMocks
	private UserServiceImpl userService;
	
	private UserDTO userDTO;
	
	@BeforeEach
	void beforeSetUpData() {
		
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
	}
	
	@Test @Order(1) @DisplayName("회원가입")
	void testRegister() {
		
		userService.register(userDTO);
		
		verify(userDAO , times(1)).register(userDTO);
	}
	
	@Test @Order(2) @DisplayName("유저 상세조회")
	void testGetUserDetail() {
		
		when(userDAO.getUserDetail(userDTO.getUserId())).thenReturn(userDTO);
		UserDTO result = userService.getUserDetail(userDTO.getUserId());
		
		assertThat(result).isNotNull();
		verify(userDAO , times(1)).getUserDetail(userDTO.getUserId());
		
	}
	
	@Test @Order(3) @DisplayName("유저 정보 업데이트")
	void testUpdateUser() throws IllegalStateException, IOException {
		
		 when(userDAO.getUserDetail(userDTO.getUserId())).thenReturn(userDTO);
		 userDTO.setProfileUUID(UUID.randomUUID()+".jpg");
		 userDTO.setProfileOriginal("test.jpg");
		 userDTO.setNickname("오영택");
		 // MockMultipartFile 생성
	    MultipartFile uploadProfile = new MockMultipartFile(
	        "file",                        // 파일 이름
	        "test.jpg",                    // 원본 파일명
	        "image/jpeg",                   // 콘텐츠 타입
	        new byte[0]                     // 파일 데이터 (빈 파일)
	    );
		userService.updateUser(uploadProfile, userDTO);
		
		System.out.println(userDTO);
		verify(userDAO , times(1)).updateUser(userDTO);
	}
	
	@Test @Order(4) @DisplayName("유저 정보 삭제")
	void testDeleteUser() {
		userService.deleteUser(userDTO.getUserId());
		
		verify(userDAO , times(1)).deleteUser(userDTO.getUserId());
	}
	
 }
