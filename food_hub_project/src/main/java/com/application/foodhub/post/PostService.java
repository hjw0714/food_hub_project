package com.application.foodhub.post;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface PostService {

	public List<Map<String, Object>> getPostList();
	
	public long getAllPostCnt();

	public long getPostCnt();

	public Long createPost(PostDTO postDTO);

	public Map<String, Object> getPostDetail(long postId, boolean isIncreaseReadCnt);

	 public Page<PostDTO> getAllPosts(PageRequest pageRequest);

	 public List<Map<String, Object>> myPostList(String userId);

}
