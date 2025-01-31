package com.application.foodhub.user;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserServiceImpl implements UserService{

	@Value("${file.repo.path}")
	private String fileRepositoryPath;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public void register(UserDTO userDTO) {
		
		if(userDTO.getEmailYn() == null) userDTO.setEmailYn("N");
		if(userDTO.getSmsYn() == null) userDTO.setSmsYn("N");
		
		userDTO.setPasswd(passwordEncoder.encode(userDTO.getPasswd()));
		userDAO.register(userDTO);
	}

	@Override // 아이디 중복확인
	public String checkValidId(String userId) {
		
		String isValidId = "n";
		if (userDAO.checkValidId(userId) == null) {
			isValidId = "y";
		}
		
		return isValidId;
	}

	@Override
	public String checkValidNickname(String nickname) {
		String isValidNickname = "n";
		if (userDAO.checkValidNickname(nickname) == null) {
			isValidNickname = "y";
		}
		
		return isValidNickname;
	}

	@Override
	public String checkValidEmail(String email) {
		String isValidEmail = "n";
		if (userDAO.checkValidEmail(email) == null) {
			isValidEmail = "y";
		}
		
		return isValidEmail;
	}

	@Override
	public boolean login(UserDTO userDTO) {

		String encodedPasswd = userDAO.getEncodedPasswd(userDTO.getUserId());
		
		if (passwordEncoder.matches(userDTO.getPasswd(), encodedPasswd)) {
			return true;
		}
		
		return false;
	}

}
