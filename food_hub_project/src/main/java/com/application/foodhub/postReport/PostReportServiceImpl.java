package com.application.foodhub.postReport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostReportServiceImpl implements PostReportService{
	
	@Autowired
	private PostReportDAO postReportDAO;

	@Override
	public boolean reportPost(long postId, String userId, String content) {
	    if (postReportDAO.existsreportPost(postId, userId)) {
	        return false;  // ✅ 이미 신고한 경우 false 반환
	    }

	    postReportDAO.reportPost(postId, userId, content);
	    return true;  // ✅ 신고 성공 시 true 반환
	}

}
