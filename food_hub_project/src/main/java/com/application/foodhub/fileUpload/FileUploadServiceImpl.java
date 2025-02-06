package com.application.foodhub.fileUpload;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadServiceImpl implements FileUploadService {

	@Value("${file.repo.path}")
	private String fileRepositoryPath;
	@Autowired
	private FileUploadDAO fileUploadDAO;
	
	public void uploadFile(MultipartFile uploadFile, FileUploadDTO fileUploadDTO) throws IllegalStateException, IOException {
	    if (!uploadFile.isEmpty()) {
	        String originalFilename = uploadFile.getOriginalFilename();
	        fileUploadDTO.setFileName(originalFilename);

	        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
	        String uploadFiles = UUID.randomUUID() + extension;
	        fileUploadDTO.setFileUUID(uploadFiles);

	        String filePath = fileRepositoryPath + uploadFiles;
	        fileUploadDTO.setFilePath(filePath);

	        uploadFile.transferTo(new File(filePath));

	        fileUploadDAO.insertFileUpload(fileUploadDTO);
	    }
	}


	@Override
	public List<FileUploadDTO> getFileListByPostId(Long postId) {
		return fileUploadDAO.getFileListByPostId(postId);
	}


	@Override
	public FileUploadDTO getFileByUUID(String fileUUID) {
		
		return fileUploadDAO.getFileByUUID(fileUUID);
	}
}
