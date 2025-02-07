package com.application.foodhub.comment;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentDAO {
	public List<CommentDTO> getCommentsByPostId(Long postId);

	public List<CommentDTO> getParentComments(Long postId);

	public List<CommentDTO> getChildComments(Long parentId);

	public void insertComment(Map<String, Object> params);

	public void updateComment(Map<String, Object> params);

	public void deleteComment(Long commentId);

	public CommentDTO getLastInsertedComment(Map<String, Object> params);

	public CommentDTO getCommentById(Long commentId);

	public List<Map<String, Object>> myCommentList(String userId);
}
