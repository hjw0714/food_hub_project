package com.application.foodhub.post;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.application.foodhub.fileUpload.FileUploadDTO;

public interface PostService {
	
	public long countPosts(String keyword, String searchType, Long categoryId, Long subCateId);
	
	public List<Map<String, Object>> getPostList(Long categoryId, Long subCateId, String orderType, String keyword, String searchType, int pageSize, int offset);

	public Long createPost(PostDTO postDTO);

	public List<Map<String, Object>> myPostList(String userId);

	public void updatePost(PostDTO postDTO);

	// 게시글 상세보기에서 이전글 다음글 postId 가져오기
	public Long getPrevPostId(long postId, long categoryId);

	public Long getNextPostId(long postId, long categoryId);

	public String getCategoryNameById(Long categoryId);
	
	public void markPostAsDeleted(long postId);
	
	public Map<String, Object> getPostDetail(long postId, boolean isIncreaseReadCnt);

	public String getSubCateNameById(Long subCateId);
	
	public List<Map<String, Object>> getLatestPostsByCategoryId(long categoryId, int limit);
	
	public List<Map<String, Object>> searchPosts(String keyword, String searchType, Long categoryId, Long subCateId, boolean isBest, int pageSize, int offset);
	

}