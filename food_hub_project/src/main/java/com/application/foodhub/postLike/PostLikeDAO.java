package com.application.foodhub.postLike;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PostLikeDAO {
    public boolean existsPostLike(@Param("postId") long postId, @Param("userId") String userId);
    public void insertPostLike(@Param("postId") long postId, @Param("userId") String userId);
    public void deletePostLike(@Param("postId") long postId, @Param("userId") String userId);
    public int countPostLikes(@Param("postId") long postId);
}