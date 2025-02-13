package com.application.foodhub.commentReport;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentReportDAO {

	public boolean existsreportComment(@Param("commentId")long commentId, @Param("userId") String userId);

	public void reportComment(@Param("commentId")long commentId, @Param("userId") String userId, @Param("content") String content);

}
