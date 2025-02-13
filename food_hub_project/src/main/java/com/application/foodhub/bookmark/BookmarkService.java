package com.application.foodhub.bookmark;

import java.util.List;
import java.util.Map;

public interface BookmarkService {

	public boolean toggleBookmark(String userId, Long postId);
	public boolean isBookmarked(String userId, long postId);
	public List<BookmarkDTO> getBookmarksByUserId(String userId);
	public List<Map<String, Object>> myBookmarkList(String userId);

}
