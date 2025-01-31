package com.application.foodhub.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/foodhub/user")
public class UserController {
	
	@Value("${file.repo.path}")       
    private String fileRepositoryPath;
	
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/login")
	public String login() {
		return "foodhub/user/login";
	}
	
	@PostMapping("/login")
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
	
	@PostMapping("/validId")
	@ResponseBody 
	public String validId(@RequestParam("userId") String userId) {
		return userService.checkValidId(userId);
	}
	
	@PostMapping("/validNickname")
	@ResponseBody
	public String validNickname(@RequestParam("nickname") String nickname) {
		return userService.checkValidNickname(nickname);
	}
	
	@PostMapping("/validEmail")
	@ResponseBody
	public String validEmail(@RequestParam("email") String email) {
		return userService.checkValidEmail(email);
	}
	
	@PostMapping("/register")
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
	
	
	
	
	
	
}
