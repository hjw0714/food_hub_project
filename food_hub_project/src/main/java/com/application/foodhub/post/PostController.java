package com.application.foodhub.post;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.application.foodhub.comment.CommentService;
import com.application.foodhub.fileUpload.FileUploadDTO;
import com.application.foodhub.fileUpload.FileUploadService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/post")
public class PostController {
	
	@Autowired
	private PostService postService;
	
	@Autowired
    private FileUploadService fileUploadService; // 파일 업로드 서비스 추가
	
	@Autowired
	private CommentService commentService;
	


	@GetMapping("/FreeBoardPostList") // 자유 게시판
	public String FreeBoardPostList(Model model) {
		model.addAttribute("postCnt" , postService.getPostCnt()); 
		model.addAttribute("postListMap" ,  postService.getPostList());
		
		return "foodhub/post/FreeBoardPostList";
	}
	
	@GetMapping("/coopPostList")	// 협력업체 게시판
	public String coopPostList(Model model) {
		model.addAttribute("postCnt" , postService.getPostCnt()); 
		model.addAttribute("postListMap" ,  postService.getPostList());
		
		return "foodhub/post/coopPostList";
	}
	
	
	@GetMapping("/informativePostList")	// 외식업 정보 게시판
	public String informativePostList(Model model) {
		model.addAttribute("postCnt" , postService.getPostCnt()); 
		model.addAttribute("postListMap" ,  postService.getPostList());
		
		return "foodhub/post/informativePostList";
	}
	
	@GetMapping("/jobBoardPostList") // 구인구직 게시판
	public String jobBoardPostList(Model model) {
		model.addAttribute("postCnt" , postService.getPostCnt()); 
		model.addAttribute("postListMap" ,  postService.getPostList());
		
		return "foodhub/post/jobBoardPostList";
	}
	
	@GetMapping("/QnAPostList") // 질문 게시판
	public String QnAPostList(Model model) {
		model.addAttribute("postCnt" , postService.getPostCnt()); 
		model.addAttribute("postListMap" ,  postService.getPostList());
		
		return "foodhub/post/QnAPostList";
	}
	
	@GetMapping("/storePromoPostList")	// 매장홍보 게시판
	public String storePromoPostList(Model model) {
		model.addAttribute("postCnt" , postService.getPostCnt()); 
		model.addAttribute("postListMap" ,  postService.getPostList());
		
		return "foodhub/post/storePromoPostList";
	}
	
	@GetMapping("/salePostList")	// 판매 게시판
	public String salePostList(Model model) {
		model.addAttribute("postCnt" , postService.getPostCnt()); 
		model.addAttribute("postListMap" ,  postService.getPostList());
		
		return "foodhub/post/salePostList";
	}
	
	@GetMapping("/allPostList")	// 전체 게시판
	public String allPostList(Model model) {
		
		model.addAttribute("postCnt" , postService.getPostCnt()); // 전체 게시판 게시글 숫자
		model.addAttribute("postListMap" , postService.getPostList()); // 게시판관련 정보 전달
		
		return "foodhub/post/allPostList";
	}
	
	@GetMapping("/createPost") 	// 게시글 작성
	public String createPost() {
		return "foodhub/post/createPost";
	}

	@PostMapping("/createPost")
	@ResponseBody
	public String createPost(@RequestParam("file[]") List<MultipartFile> files , @ModelAttribute PostDTO postDTO , HttpSession session) {
		
		
		String userId = (String) session.getAttribute("userId");
		postDTO.setUserId(userId);
		
		postService.createPost(postDTO);
		
		Long postId = postService.createPost(postDTO);  // ✅ 게시글 저장 후 postId를 받아옴
	    postDTO.setPostId(postId);  // ✅ postDTO에도 설정

		
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
	
	@GetMapping("/board")
	public String getAllPosts(@RequestParam(defaultValue = "1") int page, Model model) {
	    int pageSize = 10; // 한 페이지당 게시글 개수
	    Page<PostDTO> postPage = postService.getAllPosts(PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt")));

	    int totalPages = postPage.getTotalPages();
	    
	    // 게시글이 없을 때 빈 리스트를 반환하도록 처리
	    model.addAttribute("posts", postPage.hasContent() ? postPage.getContent() : List.of());
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalPages > 0 ? totalPages : 1);

	    return "foodhub/post/allPostList"; // Thymeleaf 템플릿 경로
	}


	
	@GetMapping("/postDetail")	// 게시글 상세보기 
	public String postDetail(Model model , @RequestParam("postId") long postId) {
		
		model.addAttribute("postMap" , postService.getPostDetail(postId , true)); // 1개의 게시글 관련정보 반환 (+조회수 증가)
		model.addAttribute("allCommentCnt" , commentService.getCommentCnt(postId));	  // 총 댓글의 갯수를 반환
		model.addAttribute("commentList" , commentService.getCommentList(postId));
		model.addAttribute("fileList", fileUploadService.getFilesByPostId(postId));
		return "foodhub/post/postDetail";
	}
	
}
