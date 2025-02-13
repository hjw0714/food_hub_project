package com.application.foodhub.bookmark;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookmarkServiceImpl implements BookmarkService {

	@Autowired
    private BookmarkDAO bookmarkDAO;

    @Override
    public boolean toggleBookmark(String userId, Long postId) {
        // 북마크 여부 확인 후 토글 처리
        boolean isBookmarked = bookmarkDAO.isBookmarked(postId, userId);

        if (isBookmarked) {
            // 이미 북마크된 경우 삭제
            bookmarkDAO.removeBookmark(postId, userId);
            return false; // 삭제된 경우 false 반환
        } 
        else {
            // 북마크 추가
            bookmarkDAO.addBookmark(postId, userId);
            return true; // 추가된 경우 true 반환
        }
    }
    
    @Override
	public boolean isBookmarked(String userId, long postId) {
    	return bookmarkDAO.isBookmarked(postId, userId); // DAO 호출로 북마크 여부 확인
	}

    @Override
    public List<BookmarkDTO> getBookmarksByUserId(String userId) {
        return bookmarkDAO.getBookmarksByUserId(userId);
    }

	@Override
	public List<Map<String, Object>> myBookmarkList(String userId) {
		return bookmarkDAO.myBookmarkList(userId);
	}

}
