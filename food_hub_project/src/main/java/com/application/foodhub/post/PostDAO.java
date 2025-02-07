package com.application.foodhub.post;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PostDAO {

	public List<Map<String, Object>> getPostList(@Param("pageSize") int pageSize, @Param("offset") int offset);

	public long getAllPostCnt();

	public long getPostCnt();

	public void createPost(PostDTO postDTO);

	public Map<String, Object> getPostDetail(long postId);

	public void updateReadCnt(long postId);

	public List<Map<String, Object>> myPostList(String userId);

	public void deletePost(long postId);

	public void updatePost(PostDTO postDTO);

	public void deleteFileByUUID(String fileUUID); // 파일 UUID로 파일 삭제

	// 게시글 상세보기에서 이전글 다음글 postId 가져오기
	public Long getPrevPostId(@Param("postId") long postId, @Param("categoryId") long categoryId);

	public Long getNextPostId(@Param("postId") long postId, @Param("categoryId") long categoryId);

	public List<Map<String, Object>> getPostListByCategory(@Param("categoryId") Long categoryId,
			@Param("pageSize") int pageSize, @Param("offset") int offset);

	public long getPostCntByCategory(@Param("categoryId") Long categoryId);

	public String getCategoryName(@Param("categoryId") Long categoryId);
}