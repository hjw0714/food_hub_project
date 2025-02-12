package com.application.foodhub.comment;

import java.util.List;
import java.util.Map;

public interface CommentService {

	public List<CommentDTO> getCommentsByPostId(Long postId);

	public List<CommentDTO> getParentComments(Long postId , String userId);

	public List<CommentDTO> getChildComments(Long parentId ,String userId);

	public void insertComment(Map<String, Object> params);

	public void updateComment(Map<String, Object> params);

	public void deleteComment(Long commentId);

	public CommentDTO getLastInsertedComment(Long postId, String userId);

	public CommentDTO getCommentById(Long commentId);

	public List<Map<String, Object>> myCommentList(String userId); // 해당 유저가 쓴 댓글 불러오기

	// ✅ 추가된 메서드
    int getCommentLikeCount(Long commentId);
    boolean toggleCommentLike(Long commentId, String userId);


	
}
