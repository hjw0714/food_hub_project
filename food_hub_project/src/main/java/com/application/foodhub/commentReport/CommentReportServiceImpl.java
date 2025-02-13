package com.application.foodhub.commentReport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.foodhub.comment.CommentDAO;

@Service
public class CommentReportServiceImpl implements CommentReportService{

	@Autowired
	private CommentReportDAO commentReportDAO;
	
	@Autowired
	private CommentDAO commentDAO;

	@Override
	public boolean reportComment(long commentId, String userId, String content) {
		
		 if (commentDAO.existsByCommentId(commentId) == 0) {
	            throw new IllegalArgumentException("🚨 신고하려는 댓글이 존재하지 않습니다.");
	        }
		
		if (commentReportDAO.existsreportComment(commentId, userId)) {
	        return false;  // ✅ 이미 신고한 경우 false 반환
	    }

	    commentReportDAO.reportComment(commentId, userId, content);
	    return true;  // ✅ 신고 성공 시 true 반환
	}
	
}
