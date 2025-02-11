package com.application.foodhub.postLike;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostLikeDAO {
    public boolean existsPostLike(long postId, String userId);
    public void insertPostLike(long postId, String userId);
    public void deletePostLike(long postId, String userId);
    public int countPostLikes(long postId);
}