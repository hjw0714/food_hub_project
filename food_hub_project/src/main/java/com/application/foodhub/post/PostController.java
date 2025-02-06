package com.application.foodhub.post;

import java.io.IOException;
import java.security.Principal;
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

	/*
	 * @GetMapping("/FreeBoardPostList") // 자유 게시판 public String
	 * FreeBoardPostList(Model model) { model.addAttribute("postCnt" ,
	 * postService.getPostCnt()); model.addAttribute("postListMap" ,
	 * postService.getPostList());
	 * 
	 * return "foodhub/post/FreeBoardPostList"; }
	 * 
	 * @GetMapping("/coopPostList") // 협력업체 게시판 public String coopPostList(Model
	 * model) { model.addAttribute("postCnt" , postService.getPostCnt());
	 * model.addAttribute("postListMap" , postService.getPostList());
	 * 
	 * return "foodhub/post/coopPostList"; }
	 * 
	 * 
	 * @GetMapping("/informativePostList") // 외식업 정보 게시판 public String
	 * informativePostList(Model model) { model.addAttribute("postCnt" ,
	 * postService.getPostCnt()); model.addAttribute("postListMap" ,
	 * postService.getPostList());
	 * 
	 * return "foodhub/post/informativePostList"; }
	 * 
	 * @GetMapping("/jobBoardPostList") // 구인구직 게시판 public String
	 * jobBoardPostList(Model model) { model.addAttribute("postCnt" ,
	 * postService.getPostCnt()); model.addAttribute("postListMap" ,
	 * postService.getPostList());
	 * 
	 * return "foodhub/post/jobBoardPostList"; }
	 * 
	 * @GetMapping("/QnAPostList") // 질문 게시판 public String QnAPostList(Model model)
	 * { model.addAttribute("postCnt" , postService.getPostCnt());
	 * model.addAttribute("postListMap" , postService.getPostList());
	 * 
	 * return "foodhub/post/QnAPostList"; }
	 * 
	 * @GetMapping("/storePromoPostList") // 매장홍보 게시판 public String
	 * storePromoPostList(Model model) { model.addAttribute("postCnt" ,
	 * postService.getPostCnt()); model.addAttribute("postListMap" ,
	 * postService.getPostList());
	 * 
	 * return "foodhub/post/storePromoPostList"; }
	 * 
	 * @GetMapping("/salePostList") // 판매 게시판 public String salePostList(Model
	 * model) { model.addAttribute("postCnt" , postService.getPostCnt());
	 * model.addAttribute("postListMap" , postService.getPostList());
	 * 
	 * return "foodhub/post/salePostList"; }
	 */
	
	@GetMapping("/allPostList")
	public String allPostList(@RequestParam(name = "page", defaultValue = "1") int page, Model model) {
		final int pageSize = 15; // 한 페이지에 보여줄 게시글 수
		long totalPosts = postService.getPostCnt(); // 전체 게시글 수
		int maxPages = (int) Math.ceil((double) totalPosts / pageSize); // 페이지 수 계산
		int offset = (page - 1) * pageSize; // 페이징 오프셋 계산

		// 게시글 목록 가져오기
		List<Map<String, Object>> postList = postService.getPostList(pageSize, offset);

		model.addAttribute("postListMap", postList);
		model.addAttribute("page", page);
		model.addAttribute("maxPages", maxPages);
		model.addAttribute("postCnt", totalPosts);

		return "foodhub/post/allPostList";
	}

	@GetMapping("/createPost") // 게시글 작성
	public String createPost() {
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
	    model.addAttribute("postMap", postService.getPostDetail(postId, true));

	    // 해당 게시글의 파일 목록 가져오기
	    List<FileUploadDTO> fileList = fileUploadService.getFileListByPostId(postId);
	    model.addAttribute("fileList", fileList);

	    return "foodhub/post/postDetail";
	}
	
	//게시글 삭제
	@GetMapping("/deletePost")
	public String deletePost(Model model, @RequestParam("postId") long postId, HttpServletRequest request) {
		HttpSession session = request.getSession();
		
		String sessionNickname = (String) session.getAttribute("nickname");
		
		model.addAttribute("postId", postId);
		model.addAttribute("postMap" , postService.getPostDetail(postId, false));
		model.addAttribute("sessionNickname", sessionNickname);
		
		return "foodhub/post/deletePost";
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
	    model.addAttribute("fileList", fileList);  // 기존 파일 목록 추가

	    return "foodhub/post/updatePost";
	}
	
	@PostMapping("/updatePost")
	@ResponseBody
	public String updatePost(@RequestParam("file[]") List<MultipartFile> files,
	                         @RequestParam(value = "deleteFiles", required = false) List<String> deleteFiles,
	                         @ModelAttribute PostDTO postDTO,
	                         HttpSession session) {
	    
	    String userId = (String) session.getAttribute("userId");
	    postDTO.setUserId(userId);
	    
	    String jsScript = """
								
				""";
		
		
	    try {
	        //  게시글 업데이트 실행
	        postService.updatePost(postDTO);
	        Long postId = postDTO.getPostId();

//	        // ✅ 기존 파일 삭제 (체크된 파일만 삭제)
//	        if (deleteFiles != null && !deleteFiles.isEmpty()) {
//	            for (String fileUUID : deleteFiles) {
//	                fileUploadService.deleteFileByUUID(fileUUID);
//	            }
//	        }
//
//	        // ✅ 새로운 파일 업로드
//	        for (MultipartFile file : files) {
//	            if (!file.isEmpty()) {
//	                FileUploadDTO fileUploadDTO = new FileUploadDTO();
//	                fileUploadDTO.setPostId(postId);
//	                fileUploadService.uploadFile(file, fileUploadDTO);
//	            }
//	        }
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


	
}
