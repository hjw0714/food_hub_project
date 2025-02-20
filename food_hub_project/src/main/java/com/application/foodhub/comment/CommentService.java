package com.application.foodhub.comment;

import java.util.List;
import java.util.Map;

public interface CommentService {

	public List<CommentDTO> getCommentsByPostId(Long postId);

	public List<CommentDTO> getParentComments(Long postId , String userId , String sortOrder);

	public List<CommentDTO> getChildComments(Long parentId ,String userId);

	public void insertComment(CommentDTO commentDTO);

	public void updateComment(CommentDTO commentDTO);

	public void deleteComment(Long commentId);

	public CommentDTO getLastInsertedComment(Long postId, String userId);

	public CommentDTO getCommentById(Long commentId);

	public List<Map<String, Object>> myCommentList(String userId); // 해당 유저가 쓴 댓글 불러오기

    public boolean isCommentDeleted(long commentId);

    public void commentsDeletedByPostId(long postId);
    
    public int countTotalCommentsByPostId(long postId);


	
}
