package com.application.foodhub.post;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
public class PostDaoTest {

	@Autowired
	private PostDAO postDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	private UserDTO userDTO;

	private PostDTO postDTO;

	private long testPostId;

	@BeforeEach
	void setUpTestData() {
		userDTO = new UserDTO();
		postDTO = new PostDTO();
		
		userDTO.setUserId("테스트 유저");
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
		
		userDAO.register(userDTO);

		postDTO.setUserId("테스트 유저");
		postDTO.setNickname("yocasu1");
		postDTO.setCategoryId(0l); // 카테고리 아이디는 0~7까지 있음
		postDTO.setSubCateId(0l); // 공지사항 카테고리에는 서브카테고리가 0만 있음
		postDTO.setCateNm("공지사항");
		postDTO.setSubCateNm("공지");
		postDTO.setTitle("테스트 제목");
		postDTO.setContent("테스트 본문");
		postDTO.setViewCnt(0l);
		postDTO.setStatus("ACTIVE");

		postDAO.createPost(postDTO);

		testPostId = postDAO.getTestPostId();
	}

	
	@Test @Order(1) @DisplayName("게시글 생성 테스트")
	void testCreatePost() {

		Map<String, Object> postDTO = postDAO.getPostDetail(testPostId);
		assertThat(postDTO).isNotNull();

	}
	

	@Test
	@Order(2)
	@DisplayName("게시글 전체조회 테스트(카테고리 상관 없이)")
	void testGetPostList() {
		List<Map<String, Object>> postList = postDAO.getPostList(testPostId, testPostId, null, null, null, 1, 0); // page = 0, offset = 0

		assertThat(postList).isNotNull();
	}

	
	@Test @Order(3) @DisplayName("게시글 상세보기")
	void testGetPostDetail() {
		Map<String, Object> postDTO = postDAO.getPostDetail(testPostId);

		assertThat(postDTO).isNotEmpty();
	}
	
	
	@Test @Order(4) @DisplayName("게시글 수정")
	void testUpdatePost() {
		Map<String, Object> before = postDAO.getPostDetail(testPostId);
		System.out.println(before);
		
		PostDTO updatePost = new PostDTO();
		
		updatePost.setPostId((long)before.get("postId"));
		updatePost.setCategoryId((long)before.get("categoryId"));
		updatePost.setSubCateId((long)before.get("subCateId"));
		updatePost.setSubCateNm(before.get("subCateNm").toString());
		updatePost.setViewCnt((long)before.get("viewCnt"));
		updatePost.setStatus(before.get("status").toString());
		
		updatePost.setTitle("수정된 제목");
		updatePost.setContent("수정된 본문");
		
		postDAO.updatePost(updatePost);
		
		Map<String, Object> after = postDAO.getPostDetail(testPostId);
		
		assertThat(after)
	    .extracting(map -> map.get("title"), map -> map.get("content"))
	    .contains("수정된 제목", "수정된 본문");
		
	}
	
	
	@Test @Order(5) @DisplayName("게시글 삭제")
	void testMarkPostAsDeleted() {
		
		postDAO.markPostAsDeleted(testPostId);
		
		Map<String, Object> deletePost = postDAO.getPostDetail(testPostId); 
		// ststus가 deleted이면 getPostDetail이 작동을 안해서 null로 출력
		
		assertThat(deletePost).isNull();
		
	}

}
