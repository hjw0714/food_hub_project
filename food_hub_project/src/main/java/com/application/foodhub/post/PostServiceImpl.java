package com.application.foodhub.post;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {

	@Autowired
	private PostDAO postDAO;
	
	
	@Override
	public List<Map<String, Object>> getPostList() {
		return postDAO.getPostList();
	}

	@Override
	public long getAllPostCnt() {
		return postDAO.getAllPostCnt();
	}

	@Override
	public long getPostCnt() {
		return postDAO.getPostCnt();
	}

	@Override
	public Map<String, Object> getPostDetail(long postId, boolean isIncreaseReadCnt) {
		
		if (isIncreaseReadCnt) {
			postDAO.updateReadCnt(postId); // 조회수 증가
		}
		return postDAO.getPostDetail(postId);
	}

	@Override
	public Long createPost(PostDTO postDTO) {
		postDAO.createPost(postDTO);
		return postDTO.getPostId();
	}

	 @Override
	    public Page<PostDTO> getAllPosts(PageRequest pageRequest) {
	        int pageSize = pageRequest.getPageSize();
	        int startRow = pageRequest.getPageNumber() * pageSize;

	        // ✅ MyBatis에서 페이징된 게시글 목록 가져오기
	        List<PostDTO> postList = postDAO.getAllPosts(startRow, pageSize);

	        // ✅ 전체 게시글 개수 가져오기
	        long totalPosts = postDAO.getAllPostCnt();

	        // ✅ PageImpl을 사용하여 Page<PostDTO> 객체 생성
	        return new PageImpl<>(postList, pageRequest, totalPosts);
	    }

	   @Override
	   public List<Map<String, Object>> myPostList(String userId) {
	       return postDAO.myPostList(userId);
	   } 
	 
//	@Override
//	public Page<PostDTO> getAllPosts(PageRequest pageRequest) {
//	    // 데이터베이스에서 페이징된 게시글 가져오기
//	    Page<PostDTO> postPage = postDAO.getAllPosts(pageRequest);
//
//	    // Post -> PostDTO 변환 (Entity -> DTO)
//	    return postPage.map(post -> new PostDTO(
//	        post.getPostId(),
//	        post.getUserId(),
//	        post.getCategoryId(),
//	        post.getSubCateId(),
//	        post.getCateNm(),
//	        post.getSubCateNm(),
//	        post.getTitle(),
//	        post.getContent(),
//	        post.getViewCnt(),
//	        post.getCreatedAt(),
//	        post.getUpdatedAt(),
//	        postDAO.countLikesByPostId(post.getPostId()) // 좋아요 수 계산
//	    ));
//	}

}
