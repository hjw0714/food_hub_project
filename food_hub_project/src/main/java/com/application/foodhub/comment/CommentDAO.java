package com.application.foodhub.comment;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentDAO {
	public List<CommentDTO> getCommentsByPostId(Long postId);

	//public List<CommentDTO> getParentComments(Long postId);

	//public List<CommentDTO> getChildComments(Long parentId);

	public void insertComment(Map<String, Object> params);

	public void updateComment(Map<String, Object> params);

	public void deleteComment(Long commentId);

	public CommentDTO getLastInsertedComment(Map<String, Object> params);

	public CommentDTO getCommentById(Long commentId);

	public List<Map<String, Object>> myCommentList(String userId);

	public void markCommentsAsDeletedByPostId(@Param("postId") long postId); /////////

	public List<CommentDTO> getParentComments(Map<String, Object> params);

	public List<CommentDTO> getChildComments(Map<String, Object> params);
	
	 // ✅ 추가된 메서드
	public int getCommentLikeCount(Long commentId); // 댓글 추천 수 조회
    public int checkUserLikedComment(Map<String, Object> params); // 특정 유저가 댓글을 추천했는지 확인
    public void insertCommentLike(Map<String, Object> params); // 댓글 추천 추가
    public void deleteCommentLike(Map<String, Object> params); // 댓글 추천 취소

    public int existsByCommentId(@Param("commentId") long commentId);
}
