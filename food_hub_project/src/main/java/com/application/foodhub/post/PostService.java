package com.application.foodhub.post;

import java.util.List;
import java.util.Map;

public interface PostService {

   
   public long getAllPostCnt();

   public long getPostCnt();

   public Long createPost(PostDTO postDTO);

   public Map<String, Object> getPostDetail(long postId, boolean isIncreaseReadCnt);

   public List<Map<String, Object>> myPostList(String userId);

   public List<Map<String, Object>> getPostList(int pageSize, int offset);
   
   
   


}