package com.application.foodhub.post;

import java.util.List;
import java.util.Map;

import com.application.foodhub.fileUpload.FileUploadDTO;

public interface PostService {

   
   public long getAllPostCnt();

   public long getPostCnt();

   public Long createPost(PostDTO postDTO);

   public Map<String, Object> getPostDetail(long postId, boolean isIncreaseReadCnt);
   
   public List<Map<String, Object>> myPostList(String userId);

   public List<Map<String, Object>> getPostList(int pageSize, int offset);
   
   public void deletePost(long postId);
   
   public void updatePost(PostDTO postDTO);
   
   // 게시글 상세보기에서 이전글 다음글 postId 가져오기
   public Long getPrevPostId(long postId, long categoryId);
   public Long getNextPostId(long postId, long categoryId);
   
}