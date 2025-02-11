package com.application.foodhub.bookmark;

import java.util.List;

public interface BookmarkService {

	public void addBookmark(Long postId, String userId);
	public void removeBookmark(Long postId, String userId);
	List<BookmarkDTO> getBookmarksByUserId(String userId);
	boolean isBookmarked(Long postId, String userId);
	public boolean toggleBookmark(Long postId, String userId);

}
