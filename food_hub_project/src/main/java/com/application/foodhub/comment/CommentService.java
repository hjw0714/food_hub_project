package com.application.foodhub.comment;

import java.util.List;
import java.util.Map;

public interface CommentService {

	public List<CommentDTO> getCommentsByPostId(Long postId);

	public List<CommentDTO> getParentComments(Long postId);

	public List<CommentDTO> getChildComments(Long parentId);

	public void insertComment(Map<String, Object> params);

	public void updateComment(Map<String, Object> params);

	void deleteComment(Long commentId);

	public CommentDTO getLastInsertedComment(Long postId, String userId);

	public CommentDTO getCommentById(Long commentId);

	

}
