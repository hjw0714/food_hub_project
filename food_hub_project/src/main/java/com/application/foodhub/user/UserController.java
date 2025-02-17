package com.application.foodhub.user;



import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.application.foodhub.bookmark.BookmarkDTO;
import com.application.foodhub.bookmark.BookmarkService;
import com.application.foodhub.comment.CommentService;
import com.application.foodhub.post.PostService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/foodhub/user")
public class UserController {
	
	@Value("${file.repo.path}")       
    private String fileRepositoryPath;
	
	
	@Autowired
	private UserService userService;

	@Autowired
	private PostService postService;
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
	private BookmarkService bookmarkService;
	
	@GetMapping("/login")	// 로그인
	public String login() {
		return "foodhub/user/login";
	}
	
	@PostMapping("/login")	// 로그인
	@ResponseBody
	public String login(@RequestBody UserDTO userDTO , HttpServletRequest request) {
		String isValidUser = "n"; // 유저 중복 검사
		
		if (userService.login(userDTO)) {
	        HttpSession session = request.getSession();
	        session.setAttribute("userId", userDTO.getUserId());

	        // 닉네임을 DB에서 가져와서 세션에 저장
	        String nickname = userService.findNicknameByUserId(userDTO.getUserId());
	        session.setAttribute("nickname", nickname);
	        
	        // 유저 정보 조회하여 membershipType 가져오기
	        UserDTO userInfo = userService.getUserDetail(userDTO.getUserId()); // DB에서 전체 정보 가져오기
	        String membershipType = userInfo.getMembershipType(); // DB에서 가져온 값 사용
	        session.setAttribute("membershipType", membershipType); // 세션에 저장

	        System.out.println("로그인 성공 - UserId: " + userDTO.getUserId() + ", 닉네임: " + nickname + ", 회원 타입: " + membershipType);

	        isValidUser = "y";
	    }
		return isValidUser;
	}
	
	@GetMapping("/register") 	// 회원가입
	public String register() {
		return "foodhub/user/register";
	}
	
	@PostMapping("/register")	//회원가입
	@ResponseBody
	public String register(@ModelAttribute UserDTO userDTO) {
		
		if (userDTO.getProfileOriginal() == null || userDTO.getProfileUUID() == null || userDTO.getProfileUUID().isEmpty()) {
			
		} 
		
		
		userService.register(userDTO);
		String jsScript = """
				<script>
					alert('회원가입 되었습니다.');
					location.href = '/foodhub/user/login';
				</script>""";
		
		return jsScript;	
	}
	
	@PostMapping("/validId")	// 아이디 중복
	@ResponseBody 
	public String validId(@RequestParam("userId") String userId) {
		return userService.checkValidId(userId);
	}
	
	@PostMapping("/validNickname")	// 닉네임 중복
	@ResponseBody
	public String validNickname(@RequestParam("nickname") String nickname) {
		return userService.checkValidNickname(nickname);
	}
	
	@PostMapping("/validEmail")	// 이메일 중복
	@ResponseBody
	public String validEmail(@RequestParam("email") String email) {
		return userService.checkValidEmail(email);
	}
	
	

    @GetMapping("/myPage") // 마이 페이지
    public String myPage(Model model, HttpServletRequest request,
                         @RequestParam(value = "postPage", defaultValue = "1") int postPage,
                         @RequestParam(value = "commentPage", defaultValue = "1") int commentPage,
                         @RequestParam(value = "bookmarkPage", defaultValue = "1") int bookmarkPage,
                         @RequestParam(value = "size", defaultValue = "5") int size) {

        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/foodhub/user/login"; // 세션 정보가 없으면 로그인 페이지로 이동
        }

        // 로그인한 유저가 쓴 전체 게시글 가져오기
        List<Map<String, Object>> allPosts = postService.myPostList(userId);
        Collections.reverse(allPosts);
        int totalPosts = allPosts.size();
        int totalPostPages = (int) Math.ceil((double) totalPosts / size);
        int startIndex = (postPage - 1) * size;
        int endIndex = Math.min(startIndex + size, totalPosts);
        List<Map<String, Object>> paginatedPosts = allPosts.subList(startIndex, endIndex);

        // 로그인한 유저가 작성한 전체 댓글 가져오기
        List<Map<String, Object>> allComments = commentService.myCommentList(userId);
        Collections.reverse(allComments);
        int totalComments = allComments.size();
        int totalCommentPages = (int) Math.ceil((double) totalComments / size);
        int commentStartIndex = (commentPage - 1) * size;
        int commentEndIndex = Math.min(commentStartIndex + size, totalComments);
        List<Map<String, Object>> paginatedComments = allComments.subList(commentStartIndex, commentEndIndex);

        model.addAttribute("userDTO", userService.getUserDetail(userId));
        model.addAttribute("myCommentList", paginatedComments);
        model.addAttribute("myPostList", paginatedPosts);
        model.addAttribute("currentPostPage", postPage);
        model.addAttribute("totalPostPages", totalPostPages);
        model.addAttribute("currentCommentPage", commentPage);
        model.addAttribute("totalCommentPages", totalCommentPages);
        
        // 로그인한 유저가 작성한 전체 북마크 가져오기
        List<BookmarkDTO> bookmarks = bookmarkService.getBookmarksByUserId(userId);
        int totalBookmarks = bookmarks.size();
        int totalBookmarkPages = (int) Math.ceil((double) totalBookmarks / size);
        int bookmarkStartIndex = (bookmarkPage - 1) * size;
        int bookmarkEndIndex = Math.min(bookmarkStartIndex + size, totalBookmarks);
        List<BookmarkDTO> paginatedBookmarks = bookmarks.subList(bookmarkStartIndex, bookmarkEndIndex); 
        
        model.addAttribute("bookmarks", paginatedBookmarks); // 현재 페이지의 북마크 리스트
        model.addAttribute("currentBookmarkPage", bookmarkPage); // 현재 북마크 페이지
        model.addAttribute("totalBookmarkPages", totalBookmarkPages); // 총 북마크 페이지 수

        return "foodhub/user/myPage";
    }

	    
	    
	@GetMapping("/modifyPage") // 수정 페이지
	public String modifyPage(HttpServletRequest request, Model model) {
	    HttpSession session = request.getSession();
	    String userId = (String) session.getAttribute("userId");
	    if (userId != null) {
	    	model.addAttribute("userDTO", userService.getUserDetail(userId));
	    }
	    return "foodhub/user/modifyPage";
	}
		
	@GetMapping("/updateUser") // 사용자 정보 업데이트 
	public String updateUser(HttpServletRequest request , Model model) {
		
		HttpSession session = request.getSession();
		model.addAttribute("userDTO" , userService.getUserDetail((String)session.getAttribute("userId")));
		
		return "foodhub/user/updateUser";
	}
	
	@PostMapping("/updateUser")
	   @ResponseBody
	   public String updateUser(
	           @RequestParam(value = "uploadProfile", required = false) MultipartFile uploadProfile,
	           @RequestParam("existingProfileImage") String existingProfileImage,
	           @ModelAttribute UserDTO userDTO) throws IllegalStateException, IOException {

	       // 새 이미지가 업로드되지 않았으면 기존 프로필 이미지 유지
	       if (uploadProfile == null || uploadProfile.isEmpty()) {
	           userDTO.setProfileUUID(existingProfileImage);
	       }

	       userService.updateUser(uploadProfile, userDTO);

	       String jsScript = """
	               <script>
	                   alert('수정 되었습니다.');
	                   location.href = '/foodhub/user/myPage';
	               </script>""";

	       return jsScript;
	   }
	
	@GetMapping("/logout") // 로그아웃
	@ResponseBody
	public String logout(HttpServletRequest request) {
		
		HttpSession session = request.getSession(); // 세션 객체 생성
		session.invalidate(); // 세션 삭제
		
		String jsScript = """
				<script>
					alert('로그아웃 되었습니다.');
					location.href = '/foodhub';
				</script>""";
			
		return jsScript;
	}
	@GetMapping("/thumbnails") // 프로필 사진
	@ResponseBody
	public Resource thumbnails(@RequestParam("fileName") String fileName) throws MalformedURLException {
		return new UrlResource("file:" + fileRepositoryPath + fileName);
	}
	
	@GetMapping("/deleteUser") // 유저 탈퇴
	   public String deleteUser() {
	      return "foodhub/user/deleteUser";
    }
	   
	@PostMapping("/deleteUser")
    @ResponseBody
	public String deleteUser(HttpServletRequest request) {
  
		HttpSession session = request.getSession();
		userService.deleteUser((String)session.getAttribute("userId"));

		session.invalidate();
		
		String jsScript ="""
				<script>
					alert('탈퇴되었습니다.');
					location.href = '/foodhub';
				</script>
				""";

		return jsScript;
	} 
	
	@GetMapping("/findId") // 아이디 찾기
	public String findId() {
		return "foodhub/user/findId";
	}
	
	@PostMapping("/findId")
	@ResponseBody
	public String findId(@RequestBody Map<String, String> requestData) {
	    String email = requestData.get("email");
	    String tel = requestData.get("tel");
	    return userService.findId(email, tel);
	}
	
	@GetMapping("/findPassword") // 비밀번호 찾기
	public String findPassword() {
		return "foodhub/user/findPassword";
	}
	
	@PostMapping("/findPassword")
	@ResponseBody
	public String findPassword(@RequestBody Map<String, String> requestData) {
		String userId = requestData.get("userId");
	    String email = requestData.get("email");
	    String tel = requestData.get("tel");
	    return userService.findPasswd(userId, email, tel);
	}
	

	@PostMapping("/resetPassword") // 비밀번호 리셋
	@ResponseBody
	public String resetPassword(@RequestParam("newPassword") String newPassword, 
	                            @RequestParam("userId") String userId) { 
		
	    userService.resetPassword(newPassword, userId);

	    String jsScript = """
	        <script>
	            alert('비밀번호가 변경되었습니다.');
	            location.href = '/foodhub/user/login';
	        </script>
	    """;

	    return jsScript;
	}
	
	@GetMapping("/changePassword") // 비밀번호 변경
	public String changePassword() {
		return "foodhub/user/changePassword";
	}
	
	@PostMapping("/changePassword")
	@ResponseBody
	public String changePassword(@RequestParam("newPassword") String newPassword, 
	                            @RequestParam("userId") String userId) { 
		
	    userService.resetPassword(newPassword, userId);

	    String jsScript = """
	        <script>
	            alert('비밀번호가 변경되었습니다.');
	            location.href = '/foodhub/user/myPage';
	        </script>
	    """;

	    return jsScript;
	}

}
