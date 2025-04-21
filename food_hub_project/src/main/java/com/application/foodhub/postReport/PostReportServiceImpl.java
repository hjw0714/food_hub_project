package com.application.foodhub.postReport;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.foodhub.stats.StatsDAO;

@Service
public class PostReportServiceImpl implements PostReportService{
	
	@Autowired
	private PostReportDAO postReportDAO;
	
	@Autowired
	private StatsDAO statsDAO;

	@Override
	public boolean reportPost(long postId, String userId, String content) {
	    if (postReportDAO.existsreportPost(postId, userId)) {
	        return false;  // ✅ 이미 신고한 경우 false 반환
	    }

	    postReportDAO.reportPost(postId, userId, content);
	    
	    int categoryId = 15;
	    String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	    
	    
	    return true;  // ✅ 신고 성공 시 true 반환
	}

}
