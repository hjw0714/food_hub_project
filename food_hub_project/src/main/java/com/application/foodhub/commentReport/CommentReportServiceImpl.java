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
	public CommentReportDTO reportComment(CommentReportDTO commentReportDTO) {
	    long commentId = commentReportDTO.getCommentId();
	    String userId = commentReportDTO.getUserId();
	    String content = commentReportDTO.getContent();

	    if (commentDAO.existsByCommentId(commentId) == 0) {
	        throw new IllegalArgumentException("🚨 신고하려는 댓글이 존재하지 않습니다.");
	    }

	    if (commentReportDAO.existsreportComment(commentId, userId)) {
	        // ❌ 이미 신고한 경우
	        commentReportDTO.setSuccess(false);
	        commentReportDTO.setMessage("이미 신고한 댓글입니다.");
	    } else {
	        // ✅ 신고 성공
	        commentReportDAO.reportComment(commentId, userId, content);
	        commentReportDTO.setSuccess(true);
	        
	        
	        commentReportDTO.setMessage("댓글이 신고되었습니다.");
	    }

	    // 📌 리디렉션 URL 설정 (신고 후 게시글 상세 페이지로 이동)
	    commentReportDTO.setRedirectUrl("/foodhub/post/postDetail?postId=" + commentId);

	    return commentReportDTO;
	}

	
}
