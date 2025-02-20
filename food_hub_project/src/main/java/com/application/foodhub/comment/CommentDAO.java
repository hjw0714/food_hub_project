package com.application.foodhub.comment;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CommentDAO {
	public List<CommentDTO> getCommentsByPostId(Long postId);

	//public List<CommentDTO> getParentComments(Long postId);

	//public List<CommentDTO> getChildComments(Long parentId);

	public void insertComment(CommentDTO commentDTO);

	public void updateComment(CommentDTO commentDTO);

	public void deleteComment(Long commentId);

	public CommentDTO getLastInsertedComment(Map<String, Object> params);

	public CommentDTO getCommentById(Long commentId);

	public List<Map<String, Object>> myCommentList(String userId);

	public void commentsDeletedByPostId(@Param("postId") long postId); /////////

	public List<CommentDTO> getParentComments(Map<String, Object> params);

	public List<CommentDTO> getChildComments(Map<String, Object> params);

    public int existsByCommentId(@Param("commentId") long commentId);
    
    public int countTotalCommentsByPostId(@Param("postId") long postId);

    @Select("""
    		SELECT  MAX(COMMENT_ID)
    		FROM 	COMMENTS
    			""")
    	public long getTestCommentId();

}
