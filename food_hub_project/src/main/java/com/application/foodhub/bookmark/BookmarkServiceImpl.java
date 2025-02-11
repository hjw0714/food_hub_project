package com.application.foodhub.bookmark;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookmarkServiceImpl implements BookmarkService {

	@Autowired 
	private BookmarkDAO bookmarkDAO;

	@Override
	public void addBookmark(Long postId, String userId) {
		bookmarkDAO.addBookmark(postId, userId);
	}

	@Override
	public void removeBookmark(Long postId, String userId) {
		bookmarkDAO.removeBookmark(postId, userId);
	}

	@Override
	public List<BookmarkDTO> getBookmarksByUserId(String userId) {
		return bookmarkDAO.getBookmarksByUserId(userId);
	}

	@Override
	public boolean isBookmarked(Long postId, String userId) {
		return bookmarkDAO.isBookmarked(postId, userId);
	}

	@Override
	public boolean toggleBookmark(Long postId, String userId) {
	    if (isBookmarked(postId, userId)) {
	        removeBookmark(postId, userId);
	        return false;
	    } else {
	        addBookmark(postId, userId);
	        return true;
	    }
	}


}
