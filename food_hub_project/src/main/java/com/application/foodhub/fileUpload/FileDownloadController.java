package com.application.foodhub.fileUpload;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class FileDownloadController {

    @Value("${file.repo.path}") // "C:/team_project/" 값을 application.properties에서 가져옴
    private String fileRepositoryPath;
    
    @Autowired
    private FileUploadService fileUploadService; 

    @GetMapping("/uploads/{fileUUID}")
    public ResponseEntity<Resource> serveFile(@PathVariable("fileUUID") String fileUUID) {
        try {
            // ✅ DB에서 fileUUID로 파일 정보 조회
            FileUploadDTO fileInfo = fileUploadService.getFileByUUID(fileUUID);
            if (fileInfo == null) {
                return ResponseEntity.notFound().build();
            }

            // ✅ 실제 저장된 파일 경로
            Path filePath = Paths.get(fileRepositoryPath).resolve(fileInfo.getFileUUID()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // ✅ 파일 확장자 확인
            String contentType = Files.exists(filePath) ? Files.probeContentType(filePath) : null;

            // ✅ contentType을 찾지 못하면, 직접 확장자로 설정
            if (contentType == null) {
                String fileName = fileInfo.getFileName().toLowerCase();
                if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                    contentType = "image/jpeg";
                } else if (fileName.endsWith(".png")) {
                    contentType = "image/png";
                } else if (fileName.endsWith(".gif")) {
                    contentType = "image/gif";
                } else {
                    contentType = "application/octet-stream"; // 기본값
                }
            }

            // ✅ 파일명 UTF-8 인코딩 적용 (브라우저 호환)
            String encodedFileName = URLEncoder.encode(fileInfo.getFileName(), StandardCharsets.UTF_8)
                                        .replaceAll("\\+", "%20");

            // ✅ 이미지: 브라우저에서 직접 보기 (inline), 일반 파일: 다운로드 (attachment)
            String contentDisposition = contentType.startsWith("image") 
                ? "inline; filename=\"" + encodedFileName + "\"" 
                : "attachment; filename*=UTF-8''" + encodedFileName;

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}