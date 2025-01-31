CREATE DATABASE MECMS;
USE MECMS;

CREATE TABLE USER (
    USER_ID 			VARCHAR(255) PRIMARY KEY,     			-- 사용자 고유 ID
    PROFILE_ORIGINAL	VARCHAR(255),                 			-- 프로필 원본 파일명 (철자 수정)
    PROFILE_UUID 		VARCHAR(255),                    	 	-- 프로필 이미지 UUID
    NICKNAME 			VARCHAR(255) NOT NULL UNIQUE,           -- 닉네임
    PASSWD				VARCHAR(255) NOT NULL,					-- 패스워드
    EMAIL 				VARCHAR(255) UNIQUE NOT NULL,           -- 이메일 (유니크 제약 조건)
    TEL 				VARCHAR(20),                            -- 전화번호
    GENDER 				CHAR(1) NOT NULL,             	-- 성별 (M: 남성, F: 여성)
    BIRTHDAY 			DATE,                                  	-- 생년월일
    MEMBERSHIP_TYPE 	ENUM('COMMON', 'BUSSI', 'ADMIN') NOT NULL, -- 멤버십 유형
    FOUNDING_AT 		DATE , 							-- 회원가입일
    BUSINESS_TYPE 		VARCHAR(255),                      		-- 사업 유형
    SMS_YN 				CHAR(1)   	 NOT NULL,            -- SMS 수신 여부 (N: 아니오, Y: 예)
	EMAIL_YN 			CHAR(1)   	 NOT NULL,            -- 이메일 수신 여부 (N: 아니오, Y: 예)
	JOIN_AT 			TIMESTAMP DEFAULT NOW(),   				-- 가입일
	MODIFY_AT 			TIMESTAMP DEFAULT NOW() ON UPDATE NOW() -- 수정일
);

-- 말머리 테이블
CREATE TABLE SUB_CATEGORY (
	SUB_CATE_ID BIGINT PRIMARY KEY AUTO_INCREMENT , -- 말머리 ID 
	SUB_CATE_NM VARCHAR(255) NOT NULL 				-- 말머리 이름
);



-- 게시글 카테고리 테이블 (수정 및 보완)
CREATE TABLE POST_CATEGORY (
    CATEGORY_ID BIGINT PRIMARY KEY AUTO_INCREMENT, -- 카테고리 ID
    CATEGORY_NM VARCHAR(255) NOT NULL,              -- 카테고리명
    SUB_CATE_ID BIGINT ,
    FOREIGN KEY (SUB_CATE_ID) REFERENCES SUB_CATEGORY(SUB_CATE_ID)
);


-- 게시글 테이블
CREATE TABLE POST (
    POST_ID 			BIGINT PRIMARY KEY AUTO_INCREMENT ,  								-- 게시글 아이디
    USER_ID 			VARCHAR(255) NOT NULL,              								-- 유저 아이디
    CATEGORY_ID 		BIGINT NOT NULL,                 									-- 카테고리 아이디 추가
    TITLE 				VARCHAR(255) NOT NULL,                 								-- 제목
    CONTENT 			VARCHAR(3000) NOT NULL,              								-- 본문
    VIEW_CNT 			BIGINT NOT NULL DEFAULT 0,          								-- 조회수
    STATUS 				ENUM('ACTIVE', 'DELETED') DEFAULT 'ACTIVE', 						-- 게시글 상태
    CREATED_AT         	TIMESTAMP DEFAULT NOW(), 			 								-- 작성일 (NOW() 적용)
    UPDATED_AT         	TIMESTAMP DEFAULT NOW() ON UPDATE NOW(),							-- 수정일 (자동 갱신)
    FOREIGN KEY (USER_ID) REFERENCES USER(USER_ID) ON DELETE CASCADE,  						-- 유저와 연결
    FOREIGN KEY (CATEGORY_ID) REFERENCES POST_CATEGORY(CATEGORY_ID) ON DELETE CASCADE  		-- 카테고리 연결
);

CREATE TABLE FILE_UPLOAD (
    FILE_ID         BIGINT PRIMARY KEY AUTO_INCREMENT,
    POST_ID         BIGINT DEFAULT NULL,             -- 연결된 게시글 ID (없을 수도 있음)
    FILE_UUID		VARCHAR (255) NOT NULL,			 -- 파일 UUID
    FILE_NAME       VARCHAR(255) NOT NULL,           -- 파일 원본 이름
    FILE_PATH       VARCHAR(500) NOT NULL,           -- 서버 저장 경로
    UPLOAD_DATE     TIMESTAMP DEFAULT NOW(),         -- 업로드 날짜
    FOREIGN KEY (POST_ID) REFERENCES POST(POST_ID) ON DELETE CASCADE
);

-- 댓글 테이블
CREATE TABLE COMMENTS (
    COMMENT_ID 		BIGINT PRIMARY KEY AUTO_INCREMENT ,  						-- 댓글 고유아이디
    POST_ID 		BIGINT NOT NULL,                       						-- 댓글이 달린 게시글 아이디
    USER_ID 		VARCHAR(255) NOT NULL,                 						-- 댓글 단 유저 아이디
    PARENT_ID 		BIGINT DEFAULT NULL,                 						-- 대댓글 관리 (NULL이면 원댓글)
    CONTENT 		TEXT NOT NULL,                          					-- 댓글 내용
    CREATED_AT      TIMESTAMP DEFAULT NOW(), 			 						-- 작성일 (NOW() 적용)
    UPDATED_AT      TIMESTAMP DEFAULT NOW() ON UPDATE NOW(),					-- 수정일 (자동 갱신)
    STATUS 			ENUM('VISIBLE', 'DELETED') DEFAULT 'VISIBLE', 				-- 댓글의 유무 상태
    FOREIGN KEY (POST_ID) REFERENCES POST(POST_ID) ON DELETE CASCADE,  			-- 게시글 연결
    FOREIGN KEY (USER_ID) REFERENCES USER(USER_ID) ON DELETE CASCADE,  			-- 유저 연결
    FOREIGN KEY (PARENT_ID) REFERENCES COMMENTS(COMMENT_ID) ON DELETE CASCADE  	-- 원댓글과 대댓 연결
);

-- 게시글 좋아요 테이블
CREATE TABLE POST_LIKE (
    POST_ID 		BIGINT NOT NULL,                     				-- 좋아요 한 게시글 아이디
    USER_ID 		VARCHAR(255) NOT NULL,                				-- 유저 아이디
   	STATUS 			ENUM('LIKED', 'CANCELLED') DEFAULT 'LIKED', 		-- 좋아요 상태
    CREATED_AT 		TIMESTAMP DEFAULT NOW(), 							-- 좋아요 누른 시간
    FOREIGN KEY (POST_ID) REFERENCES POST(POST_ID) ON DELETE CASCADE,  	-- 게시글 연결
    FOREIGN KEY (USER_ID) REFERENCES USER(USER_ID) ON DELETE CASCADE,  	-- 유저 연결
    UNIQUE KEY (POST_ID, USER_ID) 										-- 중복 좋아요 방지
);

-- 통합 댓글 및 대댓글 좋아요 테이블
CREATE TABLE COMMENT_LIKE (
    COMMENT_ID      BIGINT NOT NULL,                                           	-- 좋아요 대상 댓글/대댓글 ID
    USER_ID         VARCHAR(255) NOT NULL,                                     	-- 좋아요를 누른 사용자 ID
    STATUS          ENUM('LIKED', 'CANCELLED') DEFAULT 'LIKED',                	-- 좋아요 상태 (LIKED: 좋아요, CANCELLED: 취소)
    CREATED_AT      TIMESTAMP DEFAULT NOW(),                       				-- 좋아요 누른 시간
    FOREIGN KEY (COMMENT_ID) REFERENCES COMMENTS(COMMENT_ID) ON DELETE CASCADE, -- 댓글/대댓글과 연결
    FOREIGN KEY (USER_ID) REFERENCES USER(USER_ID) ON DELETE CASCADE,           -- 유저와 연결
    UNIQUE KEY (COMMENT_ID, USER_ID)                                            -- 중복 좋아요 방지
);

-- 게시글 신고 테이블
CREATE TABLE POST_REPORT (
    REPORT_ID 		BIGINT PRIMARY KEY AUTO_INCREMENT ,   						-- 신고 고유 ID
    POST_ID 		BIGINT NOT NULL,                     						-- 신고된 게시글 ID
    USER_ID 		VARCHAR(255) NOT NULL,               						-- 신고한 사용자 ID
    CONTENT 		VARCHAR(2000) NOT NULL,                       						-- 신고 내용(자유 텍스트 입력)
    STATUS 			ENUM('PENDING', 'REVIEWED', 'RESOLVED') DEFAULT 'PENDING', 	-- 신고 처리 상태
    CREATED_AT 		TIMESTAMP DEFAULT NOW(), 									-- 신고 등록 시간
    UPDATED_AT 		TIMESTAMP DEFAULT NOW() ON UPDATE NOW(), 					-- 처리된 시간
    FOREIGN KEY (POST_ID) REFERENCES POST(POST_ID) ON DELETE CASCADE,  			-- 게시글과 연결
    FOREIGN KEY (USER_ID) REFERENCES USER(USER_ID) ON DELETE CASCADE   			-- 유저와 연결
);

-- 댓글 대댓글 신고 테이블 
CREATE TABLE COMMENT_REPORT (
    REPORT_ID       BIGINT PRIMARY KEY AUTO_INCREMENT ,   						-- 신고 고유 ID
    COMMENT_ID      BIGINT NOT NULL,                     						-- 신고된 댓글 또는 대댓글 ID
    USER_ID         VARCHAR(255) NOT NULL,               						-- 신고한 사용자 ID
    CONTENT         VARCHAR(2000) NOT NULL,                       						-- 신고 내용(자유 텍스트 입력)
    STATUS          ENUM('PENDING', 'REVIEWED', 'RESOLVED') DEFAULT 'PENDING', 	-- 신고 처리 상태
    CREATED_AT      TIMESTAMP DEFAULT NOW(), -- 신고 등록 시간
    UPDATED_AT      TIMESTAMP DEFAULT NOW() ON UPDATE NOW(), 					-- 처리된 시간
    FOREIGN KEY (COMMENT_ID) REFERENCES COMMENTS(COMMENT_ID) ON DELETE CASCADE, -- 댓글 또는 대댓글과 연결
    FOREIGN KEY (USER_ID) REFERENCES USER(USER_ID) ON DELETE CASCADE            -- 유저와 연결
);

CREATE TABLE BOOKMARKS (							
    BOOKMARK_ID BIGINT PRIMARY KEY AUTO_INCREMENT,						-- 북마크 아이디
    USER_ID     VARCHAR(255) NOT NULL,									-- 북마크한 유저 아이디
    POST_ID     BIGINT NOT NULL,										-- 북마크한 게시글 아이디
    CREATED_AT  TIMESTAMP DEFAULT NOW(),								-- 북마크한 날짜
    FOREIGN KEY (USER_ID) REFERENCES USER(USER_ID) ON DELETE CASCADE,	
    FOREIGN KEY (POST_ID) REFERENCES POST(POST_ID) ON DELETE CASCADE,
    UNIQUE (USER_ID, POST_ID)											-- 북마크 중복 방지	
);
