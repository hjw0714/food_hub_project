package com.application.foodhub.commentLike;

public interface CommentLikeService {

	public int getCommentLikeCount(Long commentId);
    public CommentLikeDTO toggleCommentLike(Long commentId, String userId);
}
