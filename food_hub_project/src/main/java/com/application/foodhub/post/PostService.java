package com.application.foodhub.post;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.application.foodhub.fileUpload.FileUploadDTO;

public interface PostService {

	public long getAllPostCnt();

	public long getPostCnt();

	public Long createPost(PostDTO postDTO);

	public Map<String, Object> getPostDetail(long postId, boolean isIncreaseReadCnt);

	public List<Map<String, Object>> myPostList(String userId);

	public List<Map<String, Object>> getPostList(int pageSize, int offset);

	public void deletePost(long postId);

	public void updatePost(PostDTO postDTO);

	// 게시글 상세보기에서 이전글 다음글 postId 가져오기
	public Long getPrevPostId(long postId, long categoryId);

	public Long getNextPostId(long postId, long categoryId);

	public List<Map<String, Object>> getPostListByCategory(Long categoryId, int pageSize, int offset);

	public long getPostCntByCategory(Long categoryId);

	public String getCategoryName(Long categoryId);
	
	public List<Map<String, Object>> getLatestPostsByCategoryId(long categoryId);
	
	public List<Map<String, Object>> searchPostsByTitle(String keyword, int pageSize, int offset);
	public long countPostsByTitle(String keyword);

	public List<Map<String, Object>> searchPostsByTitleAndContent(String keyword, int pageSize, int offset);
	public long countPostsByTitleAndContent(String keyword);

	
	public String getCategoryNameById(Long categoryId);
	
	public long countPostsByCategoryTitle(Long categoryId, String keyword);
	public long countPostsByCategoryTitleAndContent(Long categoryId, String keyword);

	public List<Map<String, Object>> searchPostsByCategoryTitle(Long categoryId, String keyword, int pageSize, int offset);
	public List<Map<String, Object>> searchPostsByCategoryTitleAndContent(Long categoryId, String keyword, int pageSize, int offset);



}