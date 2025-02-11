package com.application.foodhub.bookmark;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface BookmarkDAO {
	
	public void addBookmark(Long postId, String userId);
	public void removeBookmark(Long postId, String userId);
	List<BookmarkDTO> getBookmarksByUserId(String userId);
	boolean isBookmarked(Long postId, String userId);

}
