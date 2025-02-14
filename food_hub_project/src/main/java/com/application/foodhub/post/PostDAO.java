package com.application.foodhub.post;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PostDAO {

	public List<Map<String, Object>> getPostList(@Param("pageSize") int pageSize, @Param("offset") int offset);
	
	public List<Map<String, Object>> getBestPostList(@Param("pageSize") int pageSize, @Param("offset") int offset);

	public long getAllPostCnt();

	public long getPostCnt();
	
	public void createPost(PostDTO postDTO);

	public Map<String, Object> getPostDetail(@Param("postId") Long postId);

	public void updateReadCnt(long postId);

	public List<Map<String, Object>> myPostList(String userId);

	 public void markPostAsDeleted(@Param("postId") long postId); ////////

	public void updatePost(PostDTO postDTO);

	public void deleteFileByUUID(String fileUUID); // 파일 UUID로 파일 삭제

	// 게시글 상세보기에서 이전글 다음글 postId 가져오기
	public Long getPrevPostId(@Param("postId") long postId, @Param("categoryId") long categoryId);

	public Long getNextPostId(@Param("postId") long postId, @Param("categoryId") long categoryId);

	public List<Map<String, Object>> getPostListByCategory(@Param("categoryId") Long categoryId,
			@Param("pageSize") int pageSize, @Param("offset") int offset);

	public long getPostCntByCategory(@Param("categoryId") Long categoryId);

	public String getCategoryName(@Param("categoryId") Long categoryId);

	// 카테고리별 최신글 2개 가져오기
	public List<Map<String, Object>> getLatestPostsByCategoryId(@Param("categoryId") long categoryId, @Param("limit")int limit);
	
	// 제목으로 검색
	public List<Map<String, Object>> searchPostsByTitle(@Param("keyword") String keyword, @Param("pageSize") int pageSize, @Param("offset") int offset);
	public long countPostsByTitle(@Param("keyword") String keyword);

	// 제목+내용으로 검색
	public List<Map<String, Object>> searchPostsByTitleAndContent(@Param("keyword") String keyword, @Param("pageSize") int pageSize, @Param("offset") int offset);
	public long countPostsByTitleAndContent(@Param("keyword") String keyword);

	public String getCategoryNameById(@Param("categoryId") Long categoryId);

	public long countPostsByCategoryTitle(@Param("categoryId") Long categoryId, @Param("keyword") String keyword);
	public long countPostsByCategoryTitleAndContent(@Param("categoryId") Long categoryId, @Param("keyword") String keyword);

	public List<Map<String, Object>> searchPostsByCategoryTitle(@Param("categoryId") Long categoryId, @Param("keyword") String keyword, @Param("pageSize") int pageSize, @Param("offset") int offset);
	public List<Map<String, Object>> searchPostsByCategoryTitleAndContent(@Param("categoryId") Long categoryId, @Param("keyword") String keyword, @Param("pageSize") int pageSize, @Param("offset") int offset);
	
	public long getBestPostCnt();
	public List<Map<String, Object>> searchBestPostsByTitle(@Param("keyword") String keyword, @Param("pageSize") int pageSize, @Param("offset") int offset);
	public List<Map<String, Object>> searchBestPostsByTitleAndContent(@Param("keyword") String keyword, @Param("pageSize") int pageSize, @Param("offset") int offset);


	public String getSubCateNameById(@Param("subCateId")Long subCateId);

	public long countPostsBySubCategoryTitle(@Param("subCateId")Long subCateId, @Param("keyword")String keyword);

	public List<Map<String, Object>> searchPostsBySubCategoryTitle(@Param("subCateId")Long subCateId, @Param("keyword")String keyword, @Param("pageSize")int pageSize,
			@Param("offset")int offset);

	public long countPostsBySubCategoryTitleAndContent(@Param("subCateId")Long subCateId, @Param("keyword")String keyword);

	public List<Map<String, Object>> searchPostsBySubCategoryTitleAndContent(@Param("subCateId")Long subCateId, @Param("keyword")String keyword,
			@Param("pageSize")int pageSize, @Param("offset")int offset);

	public long getPostCntBySubCategory(@Param("subCateId")Long subCateId);

	public List<Map<String, Object>> getPostListBySubCategory(@Param("subCateId")Long subCateId, @Param("pageSize")int pageSize, @Param("offset")int offset);

	
}