package com.application.foodhub.post;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Mapper
public interface PostDAO {
	
	public List<Map<String, Object>> getPostList();
	
	public long getAllPostCnt();

	public long getPostCnt();

	public void createPost(PostDTO postDTO);

	public Map<String, Object> getPostDetail(long postId);

	public void updateReadCnt(long postId);

	public Page<PostDTO> getAllPosts(PageRequest pageRequest);

	public Object countLikesByPostId(Long postId);
}
