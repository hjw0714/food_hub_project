package com.application.foodhub.postLike;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostLikeServiceImpl implements PostLikeService{

	@Autowired
	private PostLikeDAO postLikeDAO;
	
	@Override
	public void togglePostLike(long postId, String userId) {
		postLikeDAO.togglePostLike(postId, userId);
	}

}
