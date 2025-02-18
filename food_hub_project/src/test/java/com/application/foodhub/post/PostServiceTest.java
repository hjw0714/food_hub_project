package com.application.foodhub.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.application.foodhub.comment.CommentService;
import com.application.foodhub.postLike.PostLikeService;
import com.application.foodhub.user.UserDAO;
import com.application.foodhub.user.UserDTO;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PostServiceTest {

	@Mock
	private PostDAO postDAO;
	
	@Mock
	private UserDAO userDAO;
	
	@Mock
    private PostLikeService postLikeService;
	
	@Mock
	private CommentService commentService;
	
	@Mock
	private PasswordEncoder passwordEncoder;
	
	@InjectMocks
	private PostServiceImpl postService;
	
	private UserDTO userDTO;

	private PostDTO testPost;
	
	private long testPostId;
	
	@BeforeEach
	void setUpTestData() {
		userDTO = new UserDTO();
		testPost = new PostDTO();
		
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

		testPost.setUserId("테스트 유저");
		testPost.setNickname("yocasu1");
		testPost.setCategoryId(0l); // 카테고리 아이디는 0~7까지 있음
		testPost.setSubCateId(0l); // 공지사항 카테고리에는 서브카테고리가 0만 있음
		testPost.setCateNm("공지사항");
		testPost.setSubCateNm("공지");
		testPost.setTitle("테스트 제목");
		testPost.setContent("테스트 본문");
		testPost.setViewCnt(0l);
		testPost.setStatus("ACTIVE");


		
		testPostId = postDAO.getTestPostId();
	}
	
	@Test @Order(1) @DisplayName("게시글 작성 테스트")
	void testCreatePost() {
		postService.createPost(testPost);
		System.out.println(testPost);
		verify(postDAO , times(1)).createPost(testPost);
	}
	
	@Test @Order(2) @DisplayName("게시글 전체 조회 테스트")
	void testPostList() {
		List<Map<String, Object>> temp = new ArrayList<Map<String,Object>>();
		Map<String, Object> a = new HashMap<String, Object>();
		a.put("property1", "test1");
		a.put("property2", "test2");
		a.put("property3", "test3");
		temp.add(a);
		Map<String, Object> b = new HashMap<String, Object>();
		b.put("property1", "test1");
		b.put("property2", "test2");
		b.put("property3", "test3");
		temp.add(b);
		
		for (Map<String, Object> map : temp) {
			System.out.println(map);
		}
		
		when(postService.getPostList(testPostId, testPostId, null, null, null, 1, 0)).thenReturn(temp);
		
		List<Map<String, Object>> testList = postService.getPostList(testPostId, testPostId, null, null, null, 1, 0);
		
		assertThat(testList)
		.isNotNull()
		.isNotEmpty();
		
		verify(postDAO , times(1)).getPostList(testPostId, testPostId, null, null, null, 1, 0);
	}
	
	@Test @Order(3) @DisplayName("게시글 상세 조회 테스트")
	void testPostDetail() {
		
		when(postLikeService.getPostLikeCount(testPostId)).thenReturn(10);
		// given
		 Map<String, Object> mockPostDetail = new HashMap<>();
	        mockPostDetail.put("postId", testPostId);
	        mockPostDetail.put("title", testPost.getTitle());
	        mockPostDetail.put("content", testPost.getContent());

	        // postDAO의 getPostDetail이 mockPostDetail을 반환하도록 설정
	        when(postDAO.getPostDetail(testPostId)).thenReturn(mockPostDetail);
	        // when
	  
	        Map<String, Object> result = postService.getPostDetail(testPostId, true);
	        
	        // then
	        assertThat(result).isNotNull();
	        assertThat(result.get("title")).isEqualTo(testPost.getTitle());
	        assertThat(result.get("content")).isEqualTo(testPost.getContent());
	        assertThat(result.get("likeCount")).isEqualTo(10); // 🔹 likeCount 검증 추가

	        // DAO 및 서비스 호출 검증
	        verify(postDAO, times(1)).getPostDetail(testPostId);
	        verify(postLikeService, times(1)).getPostLikeCount(testPostId); // 🔹 호출 검증 추가
	    }
	
	@Test @Order(4) @DisplayName("게시글 수정 테스트")
	void testUpdatePost() {
		
		postService.updatePost(testPost);
		verify(postDAO , times(1)).updatePost(testPost);
		
	}
	
	@Test @Order(5) @DisplayName("게시글 삭제 테스트")
	void testDeletePost() {
		
		postService.markPostAsDeleted(testPostId);
		
		verify(postDAO , times(1)).markPostAsDeleted(testPostId);
	}
	
	
}
