package com.application.foodhub.commentLike;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentLikeDAO {

	public int getCommentLikeCount(Long commentId); // 댓글 추천 수 조회
    public int checkUserLikedComment(CommentLikeDTO commentLikeDTO); // 특정 유저가 댓글을 추천했는지 확인
    public void insertCommentLike(CommentLikeDTO commentLikeDTO); // 댓글 추천 추가
    public void deleteCommentLike(CommentLikeDTO commentLikeDTO); // 댓글 추천 취소
}
