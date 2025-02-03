package com.application.foodhub.user;

import java.io.IOException;
import java.net.MalformedURLException;

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
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/foodhub/user")
public class UserController {
	
	@Value("${file.repo.path}")       
    private String fileRepositoryPath;
	
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/login")	// 로그인
	public String login() {
		return "foodhub/user/login";
	}
	
	@PostMapping("/login")	// 로그인
	@ResponseBody
	public String login(@RequestBody UserDTO userDTO , HttpServletRequest request) {
		String isValidUser = "n";
		if (userService.login(userDTO)) {
			HttpSession session = request.getSession();
			session.setAttribute("userId", userDTO.getUserId());
			
			isValidUser = "y";
		}
		return isValidUser;
	}
	
	@GetMapping("/register") 	// 회원가입
	public String register() {
		return "foodhub/user/register";
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
	
	@PostMapping("/register")	//회원가입
	@ResponseBody
	public String register(@ModelAttribute UserDTO userDTO) {
		userService.register(userDTO);
		String jsScript = """
				<script>
					alert('회원가입 되었습니다.');
					location.href = '/foodhub/user/login';
				</script>""";
			
		return jsScript;	
		}
	
	@GetMapping("/myPage")
	public String myPage(Model model , HttpServletRequest request) {
		HttpSession session = request.getSession();
		String userId = (String)session.getAttribute("userId");
		
		model.addAttribute("userDTO" , userService.getUserDetail(userId)); 	// 유저 정보
//		model.addAttribute("myBookmarkList" , bookmarkService.myBookmark(userId));	// 유저 북마크 리스트
//		model.addAttribute("myPostList" , postService.myPostList(userId));		// 유저 게시글 리스트
//		model.addAttribute("myCommentList" , commentService.myCommentList(userId)); // 유저 댓글 리스트
		
		return "foodhub/user/myPage";
	}

	    
	    
	@GetMapping("/modifyPage")
	public String modifyPage(HttpServletRequest request, Model model) {
	    HttpSession session = request.getSession();
	    String userId = (String) session.getAttribute("userId");
	    if (userId != null) {
	    	model.addAttribute("userDTO", userService.getUserDetail(userId));
	    }
	    return "foodhub/user/modifyPage";
	}
		
	@GetMapping("/updateUser")// 사용자 정보 업데이트 
	public String updateUser(HttpServletRequest request , Model model) {
		
		HttpSession session = request.getSession();
		model.addAttribute("userDTO" , userService.getUserDetail((String)session.getAttribute("userId")));
		
		return "foodhub/user/updateUser";
	}
	
	@PostMapping("/updateUser")
	@ResponseBody
	public String updateUser(@RequestParam("uploadProfile") MultipartFile uploadProfile , @ModelAttribute UserDTO userDTO) throws IllegalStateException, IOException {
		userService.updateUser(uploadProfile , userDTO);
		
		String jsScript = """
				<script>
					alert('수정 되었습니다.');
					location.href = '/foodhub/user/myPage';
				</script>""";
		
		return jsScript;
	}
	
	@GetMapping("/logout") // localhost/user/signOut 요청시 매핑
	@ResponseBody
	public String signOut(HttpServletRequest request) {
		
		HttpSession session = request.getSession(); // 세션 객체 생성
		session.invalidate(); // 세션 삭제
		
		String jsScript = """
				<script>
					alert('로그아웃 되었습니다.');
					location.href = '/foodhub';
				</script>""";
			
		return jsScript;
	}
	@GetMapping("/thumbnails")
	@ResponseBody
	public Resource thumbnails(@RequestParam("fileName") String fileName) throws MalformedURLException {
		return new UrlResource("file:" + fileRepositoryPath + fileName);
	}
	
	@GetMapping("/deleteUser")
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
					location.href = 'foodhub/index/index';
				</script>
				""";
		return jsScript;
	}
	
	
	
	
}
