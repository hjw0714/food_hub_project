package com.application.foodhub.commentReport;

public interface CommentReportService {

	public boolean reportComment(long commentId, String userId, String content);

}
