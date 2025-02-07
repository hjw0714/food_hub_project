package com.application.foodhub.post;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.application.foodhub.comment.CommentService;
import com.application.foodhub.fileUpload.FileUploadDTO;
import com.application.foodhub.fileUpload.FileUploadService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/foodhub/post")
public class PostController {

	@Autowired
	private PostService postService;

	@Autowired
	private FileUploadService fileUploadService; // 파일 업로드 서비스 추가

	@Autowired
	private CommentService commentService;

	@Autowired
	   private PostLikeService postLikeService; 
	/**
	 * 통합 게시판 목록 조회
	 */
	@GetMapping("/allPostList")
	public String allPostList(@RequestParam(name = "page", defaultValue = "1") int page, Model model) {
		final int pageSize = 15;
		long totalPosts = postService.getPostCnt();

		// 게시글이 없는 경우 최소한 1페이지는 유지
		int maxPages = (int) Math.ceil((double) totalPosts / pageSize);
		if (maxPages == 0) {
			maxPages = 1;
		}

		// 0페이지 방지: page 값이 1보다 작으면 1로 설정
		if (page < 1) {
			page = 1;
		} else if (page > maxPages) {
			page = maxPages;
		}

		int offset = (page - 1) * pageSize;
		List<Map<String, Object>> postList = postService.getPostList(pageSize, offset);

		model.addAttribute("postListMap", postList);
		model.addAttribute("page", page);
		model.addAttribute("maxPages", maxPages);
		model.addAttribute("postCnt", totalPosts);

		return "foodhub/post/allPostList";
	}

	@GetMapping("/category/{categoryId}")
	public String categoryPostList(@PathVariable("categoryId") Long categoryId,
			@RequestParam(name = "page", defaultValue = "1") int page, Model model) {
		final int pageSize = 15;
		long totalPosts = postService.getPostCntByCategory(categoryId);

		// maxPages가 0이면 최소한 1페이지는 유지
		int maxPages = (int) Math.ceil((double) totalPosts / pageSize);
		if (maxPages == 0) {
			maxPages = 1;
		}

		// 페이지 번호 검증 (0 이하인 경우 1로 설정)
		if (page < 1) {
			page = 1;
		} else if (page > maxPages) {
			page = maxPages;
		}

		int offset = (page - 1) * pageSize;
		List<Map<String, Object>> postList = postService.getPostListByCategory(categoryId, pageSize, offset);
		String categoryName = postService.getCategoryName(categoryId);

		model.addAttribute("postListMap", postList);
		model.addAttribute("categoryId", categoryId);
		model.addAttribute("categoryName", categoryName);
		model.addAttribute("page", page);
		model.addAttribute("maxPages", maxPages);

		return "foodhub/post/categoryPostList";
	}

	@GetMapping("/createPost")
	public String createPost(HttpServletRequest request) {

		HttpSession session = request.getSession();
		String userId = (String) session.getAttribute("userId");

		// 로그인 상태가 아니라면 로그인 페이지로 리다이렉트
		if (userId == null) {
			return "redirect:/foodhub/user/login";
		}

		return "foodhub/post/createPost";
	}

	@PostMapping("/createPost")
	@ResponseBody
	public String createPost(@RequestParam("file[]") List<MultipartFile> files, @ModelAttribute PostDTO postDTO,
			HttpSession session) {

		String userId = (String) session.getAttribute("userId");
		postDTO.setUserId(userId);

		String nickname = (String) session.getAttribute("nickname");
		// System.out.println("세션 닉네임: " + nickname);

		Long postId = postService.createPost(postDTO); // ✅ 게시글 저장 후 postId를 받아옴
		postDTO.setPostId(postId); // ✅ postDTO에도 설정

		for (MultipartFile file : files) {
			if (!file.isEmpty()) {
				FileUploadDTO fileUploadDTO = new FileUploadDTO();
				fileUploadDTO.setPostId(postId);
				try {
					fileUploadService.uploadFile(file, fileUploadDTO);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		String jsScript = """
				<script>
					alert('커뮤니티 게시글이 작성 되었습니다.');
					location.href = '/';
				</script>""";

		return jsScript;
	}

	/**
	 * 게시글 상세보기
	 */
	@GetMapping("/postDetail")
	public String postDetail(Model model,
			@RequestParam(value = "postId", required = false, defaultValue = "1") long postId) {

		// 게시글 상세 정보 가져오기
		Map<String, Object> postMap = postService.getPostDetail(postId, true);
		model.addAttribute("postMap", postMap);

		// 해당 게시글의 파일 목록 가져오기
		List<FileUploadDTO> fileList = fileUploadService.getFileListByPostId(postId);
		model.addAttribute("fileList", fileList);

		// 게시글의 카테고리 ID 가져오기
		Long categoryId = (long) postMap.get("categoryId");
		// System.out.println("카테고리 아이디 : " + categoryId);

		// 이전 글, 다음 글의 postId 가져오기
		Long prevPostId = postService.getPrevPostId(postId, categoryId);
		Long nextPostId = postService.getNextPostId(postId, categoryId);
		model.addAttribute("prevPostId", prevPostId);
		model.addAttribute("nextPostId", nextPostId);

		return "foodhub/post/postDetail";
	}

	// 게시글 삭제
	@GetMapping("/deletePost")
	public String deletePost(Model model, @RequestParam("postId") long postId, HttpServletRequest request) {

		HttpSession session = request.getSession();

		String sessionNickname = (String) session.getAttribute("nickname");

		// 로그인 상태가 아니라면 로그인 페이지로 리다이렉트
		if (sessionNickname == null) {
			return "redirect:/foodhub/user/login";
		}

		model.addAttribute("postId", postId);
		model.addAttribute("postMap", postService.getPostDetail(postId, false));
		model.addAttribute("sessionNickname", sessionNickname);

		return "redirect:/foodhub/post/deletePost";
	}

	@PostMapping("/deletePost")
	@ResponseBody
	public String deletePost(@RequestParam("postId") long postId) {
		postService.deletePost(postId);

		String jsScript = """
				<script>
					alert('게시글이 삭제 되었습니다.');
					location.href='/foodhub';
				</script>

				""";

		return jsScript;
	}

	// 게시글 수정
	@GetMapping("/updatePost")
	public String updatePost(Model model, @RequestParam("postId") long postId, HttpServletRequest request) {

		HttpSession session = request.getSession();
		String sessionNickname = (String) session.getAttribute("nickname");

		// 게시글 정보 가져오기
		Map<String, Object> postMap = postService.getPostDetail(postId, false);
		String postNickname = (String) postMap.get("nickname");

		// 현재 로그인한 사용자가 게시글 작성자가 아니라면 접근 차단
		if (sessionNickname == null || !sessionNickname.equals(postNickname)) {
			return "redirect:/foodhub/post/postDetail?postId=" + postId;
		}

		// 기존 첨부파일 목록 가져오기
		List<FileUploadDTO> fileList = fileUploadService.getFileListByPostId(postId);

//	    // 파일 목록이 제대로 불러와지는지 로그 출력
//	    System.out.println("파일 개수: " + fileList.size());
//	    for (FileUploadDTO file : fileList) {
//	        System.out.println("파일명: " + file.getFileName() + ", UUID: " + file.getFileUUID());
//	    }

		// 모델에 데이터 추가
		model.addAttribute("postId", postId);
		model.addAttribute("postMap", postMap);
		model.addAttribute("sessionNickname", sessionNickname);
		model.addAttribute("fileList", fileList); // 기존 파일 목록 추가

		return "foodhub/post/updatePost";
	}

	@PostMapping("/updatePost")
	@ResponseBody
	public String updatePost(@RequestParam("file[]") List<MultipartFile> files,
			@RequestParam(value = "deleteFiles", required = false) List<String> deleteFiles,
			@ModelAttribute PostDTO postDTO, HttpSession session) {

		String userId = (String) session.getAttribute("userId");
		postDTO.setUserId(userId);

		String jsScript = """

				""";

		try {
			// 게시글 업데이트 실행
			postService.updatePost(postDTO);
			Long postId = postDTO.getPostId();

			// ✅ 기존 파일 삭제 (체크된 파일만 삭제)
			if (deleteFiles != null && !deleteFiles.isEmpty()) {
				for (String fileUUID : deleteFiles) {
					fileUploadService.deleteFileByUUID(fileUUID);
				}
			}

			// ✅ 새로운 파일 업로드
			for (MultipartFile file : files) {
				if (!file.isEmpty()) {
					FileUploadDTO fileUploadDTO = new FileUploadDTO();
					fileUploadDTO.setPostId(postId);
					fileUploadService.uploadFile(file, fileUploadDTO);
				}
			}
			jsScript = """
					<script>
					alert('게시글이 성공적으로 수정되었습니다.');
					location.href='/foodhub/post/postDetail?postId=' + """ + postId + """
					</script>
					""";

		} catch (Exception e) {
			e.printStackTrace();
			jsScript = """
					<script>
					alert('게시글 수정 중 오류가 발생했습니다.');
					location.href='/foodhub/post/postDetail?postId=' + """ + postDTO.getPostId() + """
					</script>
							""";

		}
		return jsScript;
	}
	
	@GetMapping("/notification")
	public String notificationPage(Model model) {
	    List<Map<String, Object>> postList = postService.getPostList(15, 0);

	    if (postList == null) {
	        postList = new ArrayList<>(); // 빈 리스트 반환
	    }

	    model.addAttribute("postListMap", postList);
	    return "foodhub/post/notification";
	}
	
	@PostMapping("/postLike")
	   public String postLike(@RequestBody PostLikeDTO postLikeDTO) {
	      
	      long postId = postLikeDTO.getPostId();
	      String userId = postLikeDTO.getUserId();
	      
	      postLikeService.togglePostLike(postId, userId);
	      
	      //System.out.println("postid : " + postId);
	      //System.out.println("userId : " + userId);
	      
	      return "";
	   }
}
