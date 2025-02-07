package com.application.foodhub.postLike;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostLikeDAO {
	
	public void togglePostLike(long postId, String userId);

}
