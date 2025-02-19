package com.application.foodhub.bookmark;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
public interface BookmarkDAO {

	public void addBookmark(@Param("postId") Long postId, @Param("userId") String userId);
	public void removeBookmark(@Param("postId") Long postId, @Param("userId") String userId);
	public boolean isBookmarked(@Param("postId") Long postId, @Param("userId") String userId);
	public List<BookmarkDTO> getBookmarksByUserId(@Param("userId") String userId);
	public List<Map<String, Object>> myBookmarkList(String userId);
	
}
