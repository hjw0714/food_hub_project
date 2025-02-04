package com.application.foodhub.fileUpload;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
	public void uploadFile(MultipartFile uploadFile , FileUploadDTO fileUploadDTO) throws IllegalStateException, IOException;
	
	public List<FileUploadDTO> getFilesByPostId(Long postId);
	
}
