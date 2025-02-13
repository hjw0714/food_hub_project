package com.application.foodhub.post;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.application.foodhub.postLike.PostLikeService;

@Service
public class PostServiceImpl implements PostService {

	@Autowired
	private PostDAO postDAO;
	
	@Autowired
	private PostLikeService postLikeService;

	@Override
	public List<Map<String, Object>> getPostList(int pageSize, int offset) {
		return postDAO.getPostList(pageSize, offset);
	}

	@Override
	public long getAllPostCnt() {
		return postDAO.getAllPostCnt();
	}

	@Override
	public long getPostCnt() {
		long count = postDAO.getPostCnt();
		return Math.max(count, 0); // 0 미만이 되지 않도록 보장
	}

	@Override
	public Map<String, Object> getPostDetail(long postId, boolean isIncreaseReadCnt) {

		if (isIncreaseReadCnt) {
			postDAO.updateReadCnt(postId); // 조회수 증가
		}
		
		 // 기존 게시글 정보 가져오기
	    Map<String, Object> postMap = postDAO.getPostDetail(postId);

	    // 게시글이 존재하면 추천수를 추가
	    if (postMap != null) {
	        int likeCount = postLikeService.getPostLikeCount(postId);
	        postMap.put("likeCount", likeCount); // 추천수 추가
	        
	        // 프로필 이미지가 없는 경우 기본 이미지 설정
	        if (!postMap.containsKey("profileUUID") || postMap.get("profileUUID") == null || postMap.get("profileUUID").toString().isEmpty()) {
	            postMap.put("profileUUID", "default-profile.png"); // 기본 이미지 경로
	        }
	    }
	    
		return postMap;
	}

	@Override
	public Long createPost(PostDTO postDTO) {
		postDAO.createPost(postDTO);
		return postDTO.getPostId();
	}

	@Override
	public List<Map<String, Object>> myPostList(String userId) {
		return postDAO.myPostList(userId);
	}

	@Override
	public void deletePost(long postId) {
		postDAO.deletePost(postId);
	}

	@Override
	public void updatePost(PostDTO postDTO) {
		postDAO.updatePost(postDTO);
	}

	@Override
	public Long getPrevPostId(long postId, long categoryId) {
		return postDAO.getPrevPostId(postId, categoryId);
	}

	@Override
	public Long getNextPostId(long postId, long categoryId) {
		return postDAO.getNextPostId(postId, categoryId);
	}

	@Override
	public List<Map<String, Object>> getPostListByCategory(Long categoryId, int pageSize, int offset) {
		return postDAO.getPostListByCategory(categoryId, pageSize, offset);
	}

	@Override
	public long getPostCntByCategory(Long categoryId) {
		long count = postDAO.getPostCntByCategory(categoryId);
		return Math.max(count, 0); // 0 미만이 되지 않도록 보장
	}

	@Override
	public String getCategoryName(Long categoryId) {
		return postDAO.getCategoryName(categoryId);
	}

	@Override
	public List<Map<String, Object>> getLatestPostsByCategoryId(long categoryId) {
		return postDAO.getLatestPostsByCategoryId(categoryId);
	}

	@Override
	public List<Map<String, Object>> searchPostsByTitle(String keyword, int pageSize, int offset) {
	    return postDAO.searchPostsByTitle(keyword, pageSize, offset);
	}

	@Override
	public long countPostsByTitle(String keyword) {
	    return postDAO.countPostsByTitle(keyword);
	}

	@Override
	public List<Map<String, Object>> searchPostsByTitleAndContent(String keyword, int pageSize, int offset) {
	    return postDAO.searchPostsByTitleAndContent(keyword, pageSize, offset);
	}

	@Override
	public long countPostsByTitleAndContent(String keyword) {
	    return postDAO.countPostsByTitleAndContent(keyword);
	}
	
	@Override
	public String getCategoryNameById(Long categoryId) {
	    return postDAO.getCategoryNameById(categoryId);
	}
	
	@Override
	public long countPostsByCategoryTitle(Long categoryId, String keyword) {
	    return postDAO.countPostsByCategoryTitle(categoryId, keyword);
	}

	@Override
	public long countPostsByCategoryTitleAndContent(Long categoryId, String keyword) {
	    return postDAO.countPostsByCategoryTitleAndContent(categoryId, keyword);
	}

	@Override
	public List<Map<String, Object>> searchPostsByCategoryTitle(Long categoryId, String keyword, int pageSize, int offset) {
	    return postDAO.searchPostsByCategoryTitle(categoryId, keyword, pageSize, offset);
	}

	@Override
	public List<Map<String, Object>> searchPostsByCategoryTitleAndContent(Long categoryId, String keyword, int pageSize, int offset) {
	    return postDAO.searchPostsByCategoryTitleAndContent(categoryId, keyword, pageSize, offset);
	}



}
