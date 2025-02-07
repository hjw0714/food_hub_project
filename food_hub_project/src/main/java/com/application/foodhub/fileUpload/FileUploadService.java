package com.application.foodhub.fileUpload;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
	
	public void uploadFile(MultipartFile uploadFile , FileUploadDTO fileUploadDTO) throws IllegalStateException, IOException;
	
	public List<FileUploadDTO> getFileListByPostId(Long postId);
	
	public FileUploadDTO getFileByUUID(String fileUUID); //  파일 UUID로 정보 조회
	
	public void deleteFileByUUID(String fileUUID); // 파일 UUID로 파일 삭제
	
}
