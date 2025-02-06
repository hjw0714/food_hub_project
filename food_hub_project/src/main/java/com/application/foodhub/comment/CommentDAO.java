package com.application.foodhub.comment;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentDAO {
	List<CommentDTO> getCommentsByPostId(Long postId);
    
	List<CommentDTO> getParentComments(Long postId);
    
	List<CommentDTO> getChildComments(Long parentId);

    void insertComment(Map<String, Object> params);

    void updateComment(Map<String, Object> params);

    void deleteComment(Long commentId);
    

	CommentDTO getLastInsertedComment(Map<String, Object> params);

	CommentDTO getCommentById(Long commentId);
}
