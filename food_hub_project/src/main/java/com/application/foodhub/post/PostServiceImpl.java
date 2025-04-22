package com.application.foodhub.post;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.foodhub.comment.CommentService;
import com.application.foodhub.postLike.PostLikeService;

@Service
public class PostServiceImpl implements PostService {

	@Autowired
	private PostDAO postDAO;
	
	@Autowired
	private PostLikeService postLikeService;
	
	@Autowired
	private CommentService commentService;
	

	
	@Override
    public Map<String, Object> getPostDetail(long postId, boolean isIncreaseReadCnt) {
        // ✅ 삭제된 게시글인지 확인
        Map<String, Object> postMap = postDAO.getPostDetail(postId);
        if (postMap == null || "DELETED".equals(postMap.get("status"))) {
            return null; // 삭제된 게시글이면 null 반환
        }

        // ✅ 조회수 증가 처리
        if (isIncreaseReadCnt) {
            postDAO.updateReadCnt(postId);
        }

        // ✅ 추천수 추가
        int likeCount = postLikeService.getPostLikeCount(postId);
        postMap.put("likeCount", likeCount);

        // ✅ 프로필 이미지가 없는 경우 기본 이미지 설정
        if (!postMap.containsKey("profileUUID") || postMap.get("profileUUID") == null || postMap.get("profileUUID").toString().isEmpty()) {
            postMap.put("profileUUID", "default-profile.png"); // 기본 프로필 이미지
        }

        return postMap;
    }

	@Override
	public Long createPost(PostDTO postDTO) {
		postDAO.createPost(postDTO);
		
		int categoryId = 4;
		int postCategoryId = postDTO.getCategoryId().intValue() + 5;
		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		
		return postDTO.getPostId();
	}

	@Override
	public List<Map<String, Object>> myPostList(String userId) {
		return postDAO.myPostList(userId);
	}

	 @Override
	    public void markPostAsDeleted(long postId) {
	        postDAO.markPostAsDeleted(postId); // 게시글 상태 변경
	        commentService.commentsDeletedByPostId(postId); // 해당 게시글의 댓글들 숨기기
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
	public List<Map<String, Object>> getLatestPostsByCategoryId(long categoryId, int limit) {
		return postDAO.getLatestPostsByCategoryId(categoryId, limit);
	}
	
	@Override
	public String getCategoryNameById(Long categoryId) {
	    return postDAO.getCategoryNameById(categoryId);
	}
	

	@Override
	public String getSubCateNameById(Long subCateId) {
		return postDAO.getSubCateNameById(subCateId);
	}

	@Override
	public long countPosts(String keyword, String searchType, Long categoryId, Long subCateId) {
		return postDAO.countPosts(keyword, searchType, categoryId, subCateId);
	}

	@Override
	public List<Map<String, Object>> getPostList(Long categoryId, Long subCateId, String orderType, String keyword,
			String searchType, int pageSize, int offset) {
		return postDAO.getPostList(categoryId, subCateId, orderType, keyword, searchType, pageSize, offset);
	}

	@Override
	public List<Map<String, Object>> searchPosts(String keyword, String searchType, Long categoryId, Long subCateId, boolean isBest, int pageSize, int offset) {
	    return postDAO.searchPosts(keyword, searchType, categoryId, subCateId, isBest, pageSize, offset);
	}




}
