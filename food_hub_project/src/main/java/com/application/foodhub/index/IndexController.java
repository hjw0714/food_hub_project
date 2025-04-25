package com.application.foodhub.index;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.application.foodhub.banner.BannerService;
import com.application.foodhub.post.PostService;
import com.application.foodhub.postLike.PostLikeService;
import com.application.foodhub.visitorLog.VisitorLogService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class IndexController {

	@Autowired
	private PostLikeService postLikeService;

	@Autowired
	private PostService postService;

	@Autowired
	private BannerService bannerService;

	@Autowired
	private VisitorLogService visitorLogService;
	
	@Value("${file.repo.path}")
	private String fileRepositoryPath;


	@GetMapping
	public String index() {
		return "redirect:/foodhub";
	}

	@GetMapping("/foodhub")
	public String foodhub(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		String userId = (String) session.getAttribute("userId");

		System.out.println("[foodhub] 세션 ID: " + session.getId());

		// 방문자 기록
		visitorLogService.recordVisitor(request, userId);
		visitorLogService.recordVisitorDetail(request, userId);

		// 오늘 방문자 수
		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		Long visitorCnt = visitorLogService.getVisitorCnt(today);
		model.addAttribute("visitorCnt", visitorCnt);

		// 전체 방문자 수
		Long totalVisitorCnt = visitorLogService.getTotalVisitorCnt();
		model.addAttribute("totalVisitorCnt", totalVisitorCnt);

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
			List<Map<String, Object>> latestPosts = postService.getLatestPostsByCategoryId(categoryId, 2);

			// 카테고리에 데이터가 없을 경우 빈 리스트 추가
			if (latestPosts == null || latestPosts.isEmpty()) {
				latestPosts = new ArrayList<>();
			}
			categoryLatestPosts.put(categoryId, latestPosts);

		}

		// 공지사항은 따로
		List<Map<String, Object>> noticePosts = postService.getLatestPostsByCategoryId(0, 4);
		if (noticePosts == null || noticePosts.isEmpty()) {
			noticePosts = new ArrayList<>();
		}

		model.addAttribute("categoryLatestPosts", categoryLatestPosts);
		model.addAttribute("categoryNames", categoryNames);
		model.addAttribute("noticePosts", noticePosts);
		model.addAttribute("banners", bannerService.selectAllBanners());

		return "foodhub/index/index";
	}

	@GetMapping("/foodhub/banner/image")
	@ResponseBody
	public Resource getBannerImage(@RequestParam("fileName") String fileName) throws MalformedURLException {
		return new UrlResource("file:" + fileRepositoryPath + fileName);
	}

}
