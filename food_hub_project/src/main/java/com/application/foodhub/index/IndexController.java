package com.application.foodhub.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.application.foodhub.post.PostService;
import com.application.foodhub.postLike.PostLikeService;

@Controller
public class IndexController {

	@Autowired
	private PostLikeService postLikeService;

	@Autowired
	private PostService postService;

	@GetMapping
	public String index() {
		return "redirect:/foodhub";
	}

	@GetMapping("/foodhub")
	public String foodhub(Model model) {

		List<Map<String, Object>> topLikedPosts = postLikeService.getTopLikedPosts();

		// ✅ 리스트가 null이거나 비어있으면 빈 리스트로 설정
		if (topLikedPosts == null || topLikedPosts.isEmpty()) {
			topLikedPosts = new ArrayList<>();
		}

		model.addAttribute("topLikedPosts", topLikedPosts);

		Map<Integer, List<Map<String, Object>>> categoryLatestPosts = new HashMap<>();
		Map<Integer, String> categoryNames = new HashMap<>(); // 카테고리 ID와 이름 매핑

		// 카테고리 이름 설정
		categoryNames.putIfAbsent(1, "🍽️외식업정보게시판");
		categoryNames.putIfAbsent(2, "💬자유게시판");
		categoryNames.putIfAbsent(3, "🛎️알바공고게시판");
		categoryNames.putIfAbsent(4, "❓질문게시판");
		categoryNames.putIfAbsent(5, "📦중고장비거래게시판");
		categoryNames.putIfAbsent(6, "🏪매장홍보게시판");
		categoryNames.putIfAbsent(7, "🤝협력업체게시판");

		// 1~7번 카테고리별 최신 게시글 2개씩 가져오기
		for (int categoryId = 1; categoryId <= 7; categoryId++) {
			List<Map<String, Object>> latestPosts = postService.getLatestPostsByCategoryId(categoryId);

			// 카테고리에 데이터가 없을 경우 빈 리스트 추가
			if (latestPosts == null || latestPosts.isEmpty()) {
				latestPosts = new ArrayList<>();
			}
			categoryLatestPosts.put(categoryId, latestPosts);

		}

		model.addAttribute("categoryLatestPosts", categoryLatestPosts);
		model.addAttribute("categoryNames", categoryNames);

		return "foodhub/index/index";
	}

}
