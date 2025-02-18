package com.application.foodhub.comments;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.HashMap;
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

import com.application.foodhub.comment.CommentDAO;
import com.application.foodhub.comment.CommentDTO;
import com.application.foodhub.post.PostDAO;
import com.application.foodhub.post.PostDTO;
import com.application.foodhub.user.UserDAO;
import com.application.foodhub.user.UserDTO;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class CommentsDaoTest {

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private PostDAO postDAO;

	@Autowired
	private CommentDAO commentDAO;


	private long commentId;
	private long postId;
	private CommentDTO commentDTO;
	private UserDTO userDTO;
	private PostDTO postDTO;
	
	private long testPostId;
	private long testCommentId;
	

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
		
		postDTO = new PostDTO();
		postDTO.setUserId("yocasu1");
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
		
		commentDTO = new CommentDTO();
		commentDTO.setPostId(testPostId);
		commentDTO.setUserId("yocasu1");
		commentDTO.setNickname("yocasu1");
		commentDTO.setParentId(null);
		commentDTO.setContent("테스트용");
		commentDTO.setProfileUUID("qwer-asdf-zxcv");
		
		commentDAO.insertComment(commentDTO);
		testCommentId = commentDAO.getTestCommentId();
		
		
	}

	@Test
	@Order(1)
	@DisplayName("댓글 생성")
	void testInsertComment() {
		CommentDTO commentDTO = commentDAO.getCommentById(testCommentId);
		//System.out.println(commentDTO);
		assertThat(commentDTO).isNotNull();
	}
	
	@Test @Order(2) @DisplayName("댓글 조회")
	void testGetCommentList() {
		List<CommentDTO> commentList = commentDAO.getCommentsByPostId(testPostId);
		//System.out.println(commentList);
		assertThat(commentList).isNotNull();
	}
	

	
	@Test @Order(3) @DisplayName("댓글 수정")
	void testUpdateComment() {
		CommentDTO before = commentDAO.getCommentById(testCommentId);
		before.setContent("테스트1111");
		
		commentDAO.updateComment(before);
		
		CommentDTO after = commentDAO.getCommentById(testCommentId);
		System.out.println(after);
		assertThat(after)
		.extracting(CommentDTO::getContent);
		
	}
	
	@Test @Order(4) @DisplayName("댓글 삭제")
	void testDeleteComment() {
		commentDAO.deleteComment(testCommentId);
		CommentDTO commentDTO = commentDAO.getCommentById(testCommentId);
		assertThat(commentDTO.getStatus().equals("DELETED"));

	}
	
	
	
}