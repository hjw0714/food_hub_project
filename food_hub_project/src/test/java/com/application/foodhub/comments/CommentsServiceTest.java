package com.application.foodhub.comments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.application.foodhub.comment.CommentDAO;
import com.application.foodhub.comment.CommentDTO;
import com.application.foodhub.comment.CommentServiceImpl;
import com.application.foodhub.post.PostDAO;
import com.application.foodhub.post.PostDTO;
import com.application.foodhub.user.UserDAO;
import com.application.foodhub.user.UserDTO;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommentsServiceTest {

	@Mock
	private CommentDAO commentDAO;
	
	@Mock
	private UserDAO userDAO;

	@Mock
	private PasswordEncoder passwordEncoder;
	
	@Mock
	private PostDAO postDAO;
	
	@InjectMocks
	private CommentServiceImpl commentService;

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
		
		testCommentId = commentDAO.getTestCommentId();
		
		
	}
	
	@Test @Order(1) @DisplayName("댓글 작성 테스트")
	void testCreateComment() {
		commentService.insertComment(commentDTO);
		
		System.out.println(commentDTO);
		verify(commentDAO , times(1)).insertComment(commentDTO);
	}
	
	@Test @Order(2) @DisplayName("댓글 조회 테스트")
	void testCommentList() {
		
		List<CommentDTO> testList = Arrays.asList(commentDTO);
		when(commentDAO.getCommentsByPostId(testPostId)).thenReturn(testList);
		
		List<CommentDTO> result = commentService.getCommentsByPostId(testPostId);
		
		assertThat(result)
		.isNotNull()
		.isNotEmpty();
		
		verify(commentDAO, times(1)).getCommentsByPostId(testPostId);
	}
	
	@Test @Order(3) @DisplayName("댓글 수정 테스트")
	void testUpdateComment() {
		commentService.updateComment(commentDTO);
		
		verify(commentDAO , times(1)).updateComment(commentDTO);
	}
	
	@Test @Order(4) @DisplayName("댓글 삭제 테스트")
	void testDeleteComment() {
		commentService.deleteComment(testCommentId);
		verify(commentDAO, times(1)).deleteComment(testCommentId);
	}
	
	
	
	
	
	
}
