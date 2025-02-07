package com.application.foodhub.comment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

	@Autowired
	private CommentDAO commentDAO;

	// 특정 게시글의 모든 댓글 조회 (원댓글 + 대댓글 포함)
	@Override
	public List<CommentDTO> getCommentsByPostId(Long postId) {
		return commentDAO.getCommentsByPostId(postId);
	}

	// 특정 게시글의 원댓글(부모 댓글) 조회
	@Override
	public List<CommentDTO> getParentComments(Long postId) {
		List<CommentDTO> comments = commentDAO.getParentComments(postId);
		// 삭제된 댓글을 제외하는 필터링
		return comments.stream().filter(comment -> !"DELETED".equals(comment.getStatus())).toList();
	}

	// 특정 댓글의 대댓글 조회
	@Override
	public List<CommentDTO> getChildComments(Long parentId) {
		return commentDAO.getChildComments(parentId);
	}

	// 댓글 추가 (원댓글 또는 대댓글)
	@Override
	public void insertComment(Map<String, Object> params) {
		commentDAO.insertComment(params);
	}

	// 댓글 수정
	@Override
	public void updateComment(Map<String, Object> params) {
		commentDAO.updateComment(params);
	}

	// 댓글 삭제 (상태 변경)
	@Override
	public void deleteComment(Long commentId) {
		commentDAO.deleteComment(commentId);
	}

	@Override
	public CommentDTO getLastInsertedComment(Long postId, String userId) {
		Map<String, Object> params = new HashMap<>();
		params.put("postId", postId);
		params.put("userId", userId);

		return commentDAO.getLastInsertedComment(params); // DAO에서 가져온 최근 댓글 반환
	}

	@Override
	public CommentDTO getCommentById(Long commentId) {
		return commentDAO.getCommentById(commentId);
	}

	@Override
	public List<Map<String, Object>> myCommentList(String userId) {
		return commentDAO.myCommentList(userId);
	}

}
