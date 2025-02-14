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

	@Override
	public List<CommentDTO> getParentComments(Long postId, String userId, String sortOrder) {
	    Map<String, Object> params = new HashMap<>();
	    params.put("postId", postId);
	    params.put("userId", userId);
	    params.put("sortOrder", sortOrder);
	    
	    List<CommentDTO> comments = commentDAO.getParentComments(params);
	    return comments.stream().filter(comment -> !"DELETED".equals(comment.getStatus())).toList();
	}

	@Override
	public List<CommentDTO> getChildComments(Long parentId, String userId) {
	    Map<String, Object> params = new HashMap<>();
	    params.put("parentId", parentId);
	    params.put("userId", userId);
	    
	    return commentDAO.getChildComments(params);
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
	
	 // ✅ 추가된 메서드
    @Override
    public int getCommentLikeCount(Long commentId) {
        return commentDAO.getCommentLikeCount(commentId);
    }

    @Override
    public boolean toggleCommentLike(Long commentId, String userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("commentId", commentId);
        params.put("userId", userId);

        int likeExists = commentDAO.checkUserLikedComment(params);

        if (likeExists > 0) {
            commentDAO.deleteCommentLike(params);
            return false; // 추천 취소됨
        } else {
            commentDAO.insertCommentLike(params);
            return true; // 추천 추가됨
        }
    }
    
    @Override
    public boolean isCommentDeleted(long commentId) {
        return commentDAO.existsByCommentId(commentId) > 0;
    }
    
    @Override
    public void markCommentsAsDeletedByPostId(long postId) {
        commentDAO.markCommentsAsDeletedByPostId(postId);
    }

}
