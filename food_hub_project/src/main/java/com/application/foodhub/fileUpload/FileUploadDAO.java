package com.application.foodhub.fileUpload;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileUploadDAO {

	// ✅ 파일 업로드 (XML의 insertFileUpload 호출)
    public void insertFileUpload(FileUploadDTO fileUploadDTO);

	public void updateFileUpload(FileUploadDTO fileUploadDTO);

	public List<FileUploadDTO> findFilesByPostId(Long postId);
	
}
