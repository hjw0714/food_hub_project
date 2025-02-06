package com.application.foodhub.post;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PostDAO {
   
   public List<Map<String, Object>> getPostList(@Param("pageSize") int pageSize, @Param("offset") int offset);
   
   public long getAllPostCnt();

   public long getPostCnt();

   public void createPost(PostDTO postDTO);

   public Map<String, Object> getPostDetail(long postId);

   public void updateReadCnt(long postId);


   public List<Map<String, Object>> myPostList(String userId);
   
   public void deletePost(long postId);
   
   public void updatePost(PostDTO postDTO);

   
   
}