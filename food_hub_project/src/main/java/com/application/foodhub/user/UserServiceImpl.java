package com.application.foodhub.user;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.application.foodhub.stats.StatsDAO;

@Service
public class UserServiceImpl implements UserService {

	@Value("${file.repo.path}")
	private String fileRepositoryPath;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private StatsDAO statsDAO;

	@Override	// 회원 가입
	public void register(UserDTO userDTO) {
		
		if (userDTO.getEmailYn() == null)
			userDTO.setEmailYn("N");
		if (userDTO.getSmsYn() == null)
			userDTO.setSmsYn("N");

		userDTO.setPasswd(passwordEncoder.encode(userDTO.getPasswd()));
		userDTO.setStatus("ACTIVE");
		userDAO.register(userDTO);
		
		int totalCategoryId = 3;
		int joinCategoryId = 1;
		
		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		
		Long totalCount = statsDAO.getTotalUserCnt(totalCategoryId, today);
		Long joinCount = statsDAO.getJoinUserCnt(joinCategoryId, today);
		
		Long allTotalCount = statsDAO.getAllTotalCount(totalCategoryId);
		System.out.println("allTotalCount: " + allTotalCount);
		
		if(totalCount == null) {
			statsDAO.insertTotalUser(totalCategoryId, today, allTotalCount);
	    }
	    else {
	    	statsDAO.increaseTotalUserCnt(totalCategoryId, today);
	    }
		
		if(joinCount == null) {
			statsDAO.insertJoinUser(joinCategoryId, today);
		}
		else {
			statsDAO.increaseJoinUserCnt(joinCategoryId, today);
		}
		
	}

	@Override // 아이디 중복확인
	public String checkValidId(String userId) {

		String isValidId = "n";
		if (userDAO.checkValidId(userId) == null) {
			isValidId = "y";
		}

		return isValidId;
	}

	@Override	// 닉네임 중복 확인
	public String checkValidNickname(String nickname) {
		String isValidNickname = "n";
		if (userDAO.checkValidNickname(nickname) == null) {
			isValidNickname = "y";
		}

		return isValidNickname;
	}

	@Override	// 이메일 중복 확인
	public String checkValidEmail(String email) {
		String isValidEmail = "n";
		if (userDAO.checkValidEmail(email) == null) {
			isValidEmail = "y";
		}

		return isValidEmail;
	}

	@Override	// 로그인
	public boolean login(UserDTO userDTO) {

		// System.out.println("입력된 userId: " + userDTO.getUserId());

		UserDTO dbUser = userDAO.findUserForLogin(userDTO.getUserId());

	    // 유저가 존재하지 않거나, 삭제된 상태이면 로그인 실패
	    if (dbUser == null || !"ACTIVE".equals(dbUser.getStatus())) {
	        return false;
	    }

	    return passwordEncoder.matches(userDTO.getPasswd(), dbUser.getPasswd());
	    
		//String encodedPasswd = userDAO.getEncodedPasswd(userDTO.getUserId());

		//if (passwordEncoder.matches(userDTO.getPasswd(), encodedPasswd)) {
		//	return true;
		//}

		//return false;
	}

	@Override	// 유저 정보 상세 조회
	public UserDTO getUserDetail(String userId) {
		return userDAO.getUserDetail(userId);
	}

	@Override	// 사용자 정보 업데이트
	public void updateUser(MultipartFile uploadProfile, UserDTO userDTO) throws IllegalStateException, IOException {
		// 기존 프로필 사진 유지
		if (uploadProfile == null || uploadProfile.isEmpty()) {
			UserDTO existingUser = userDAO.getUserDetail(userDTO.getUserId());
			userDTO.setProfileUUID(existingUser.getProfileUUID());
			userDTO.setProfileOriginal(existingUser.getProfileOriginal());
		} else {
			// 기존 파일 삭제
			if (userDTO.getProfileUUID() != null) {
				new File(fileRepositoryPath + userDTO.getProfileUUID()).delete();
			}

			// 새로운 파일 저장
			String originalFilename = uploadProfile.getOriginalFilename();
			userDTO.setProfileOriginal(originalFilename);

			String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
			String uploadFile = UUID.randomUUID() + extension;
			userDTO.setProfileUUID(uploadFile);

			uploadProfile.transferTo(new File(fileRepositoryPath + uploadFile));
		}

		// 일반 회원으로 변경 시 창업일과 업종 null 처리
		if ("COMMON".equals(userDTO.getMembershipType())) {
			userDTO.setFoundingAt(null); // 창업일 null
			userDTO.setBusinessType(null); // 업종 null
		}

		// 사용자 정보 업데이트
		userDAO.updateUser(userDTO);
	}

	@Override
	@Transactional	// 유저 탈퇴
	public void deleteUser(String userId) {

		//String deleteProfile = userDAO.getDeleteUserProfile(userId);
		//new File(fileRepositoryPath + deleteProfile).delete();
		userDAO.softDeleteUser(userId);
		
		int deleteCategoryId = 2;
		int totalCategoryId = 3;
		
		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		
		Long deleteCount = statsDAO.getDeleteUserCnt(deleteCategoryId, today);
		Long totalCount = statsDAO.getTotalUserCnt(totalCategoryId, today);
		Long allTotalCount = statsDAO.getAllTotalCount(totalCategoryId);
		
		if(deleteCount == null) {
			statsDAO.insertDeleteUser(deleteCategoryId, today);
		}
		else {
			statsDAO.increaseDeleteUserCnt(deleteCategoryId, today);
		}
		
		if(totalCount == null) {
			statsDAO.insertTotalUser(totalCategoryId, today, allTotalCount - 1);
		}
		else {
			statsDAO.decreaseTotalUser(totalCategoryId, today);
		}
		
	}

	@Override	// 아이디 찾기
	public String findId(String email, String tel) {

		Map<String, Object> params = new HashMap<>();
		params.put("email", email);
		params.put("tel", tel);

		String userId = userDAO.findId(params);

		if (userId == null) {
			userId = "notFound";
		}
		return userId;
	}

	@Override	// 비밀번호 찾기
	public String findPasswd(String userId, String email, String tel) {

		Map<String, Object> params = new HashMap<>();

		params.put("userId", userId);
		params.put("email", email);
		params.put("tel", tel);

		String userPasswd = userDAO.findPasswd(params);

		if (userPasswd == null) {
			userPasswd = "notFound";
		}

		return userPasswd;
	}

	@Override	// 비밀번호 초기화
	public void resetPassword(String newPassword, String userId) {

//	    System.out.println(newPassword);
//	    System.out.println(userId);
//	    System.out.println(passwordEncoder.encode(newPassword));

		UserDTO userDTO = new UserDTO();
		userDTO.setUserId(userId);
		userDTO.setPasswd(passwordEncoder.encode(newPassword));

		// System.out.println("변경된 비밀번호: " + userDTO.getPasswd()); // 확인용 출력

		userDAO.resetPassword(userDTO);
	}

	@Override	// 유저아이디로 닉네임 조회
	public String findNicknameByUserId(String userId) {
		return userDAO.findNicknameByUserId(userId); 
	}

	
	
	 @Override
	    public UserDTO findByNickname(String nickname) {
	        return userDAO.findByNickname(nickname);
	    }

}
