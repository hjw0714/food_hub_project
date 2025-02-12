package com.application.foodhub.postLike;

import java.util.List;
import java.util.Map;

public interface PostLikeService {

	public void togglePostLike(long postId, String userId);
	public int getPostLikeCount(long postId);
	public List<Map<String, Object>> getTopLikedPosts();
	
}
