package com.application.foodhub.postReport;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PostReportDAO {
	
	public void reportPost(@Param("postId") Long postId, @Param("userId") String userId, @Param("content") String content);
	public boolean existsreportPost(@Param("postId") long postId, @Param("userId") String userId);

}
