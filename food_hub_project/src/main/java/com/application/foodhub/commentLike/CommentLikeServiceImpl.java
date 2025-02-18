package com.application.foodhub.commentLike;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class CommentLikeServiceImpl implements CommentLikeService {
	@Autowired
	private CommentLikeDAO commentLikeDAO;
	
	 @Override
	    public int getCommentLikeCount(Long commentId) {
	        return commentLikeDAO.getCommentLikeCount(commentId);
	    }

	 @Override
	 public CommentLikeDTO toggleCommentLike(Long commentId, String userId) {
	     CommentLikeDTO commentLikeDTO = new CommentLikeDTO();
	     commentLikeDTO.setCommentId(commentId);
	     commentLikeDTO.setUserId(userId);

	     int likeExists = commentLikeDAO.checkUserLikedComment(commentLikeDTO);

	     if (likeExists > 0) {
	         commentLikeDAO.deleteCommentLike(commentLikeDTO);
	         commentLikeDTO.setLiked(false); // 추천 취소됨
	     } else {
	         commentLikeDAO.insertCommentLike(commentLikeDTO);
	         commentLikeDTO.setLiked(true); // 추천 추가됨
	     }

	     int likeCount = commentLikeDAO.getCommentLikeCount(commentId);
	     commentLikeDTO.setLikeCount(likeCount);
	     commentLikeDTO.setSuccess(true); 

	     return commentLikeDTO;
	 }

}
