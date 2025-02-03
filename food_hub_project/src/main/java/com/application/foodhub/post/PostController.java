package com.application.foodhub.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/post")
public class PostController {
	
	@Autowired
	private PostService postService;
	
	@GetMapping("/FreeBoardPostList")
	public String FreeBoardPostList() {
		return "foodhub/post/FreeBoardPostList";
	}
	
	@GetMapping("/coopPostList")
	public String coopPostList() {
		return "foodhub/post/coopPostList";
	}
	
	@GetMapping("/createPost")
	public String createPost() {
		return "foodhub/post/createPost";
	}
	
	@GetMapping("/informativePostList")
	public String informativePostList() {
		return "foodhub/post/informativePostList";
	}
	
	@GetMapping("/jobBoardPostList")
	public String jobBoardPostList() {
		return "foodhub/post/jobBoardPostList";
	}
	
	@GetMapping("/QnAPostList")
	public String QnAPostList() {
		return "foodhub/post/QnAPostList";
	}
	
	@GetMapping("/storePromoPostList")
	public String storePromoPostList() {
		return "foodhub/post/storePromoPostList";
	}
	
	@GetMapping("/salePostList")
	public String salePostList() {
		return "foodhub/post/salePostList";
	}
	
	@GetMapping("/postDetail")
	public String postDetail() {
		return "foodhub/post/postDetail";
	}
	
}
