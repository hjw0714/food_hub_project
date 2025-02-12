package com.application.foodhub.postLike;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostLikeServiceImpl implements PostLikeService {

	@Autowired
	private PostLikeDAO postLikeDAO;

	@Override
	public void togglePostLike(long postId, String userId) {
		if (postLikeDAO.existsPostLike(postId, userId)) {
			postLikeDAO.deletePostLike(postId, userId);
		} else {
			postLikeDAO.insertPostLike(postId, userId);
		}
	}

	@Override
	public int getPostLikeCount(long postId) {
		return postLikeDAO.countPostLikes(postId);
	}

	@Override
	public List<Map<String, Object>> getTopLikedPosts() {
	    List<Map<String, Object>> topPosts = postLikeDAO.getTopLikedPosts();

	    // ✅ 리스트가 null이면 빈 리스트로 초기화
	    if (topPosts == null || topPosts.isEmpty()) {
	        return new ArrayList<>();
	    }

	    // ✅ 최대 5개만 반환
	    return (topPosts.size() > 5) ? topPosts.subList(0, 5) : topPosts;
	}


}
