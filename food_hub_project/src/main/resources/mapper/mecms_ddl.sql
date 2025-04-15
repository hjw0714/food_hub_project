CREATE DATABASE MECMS;
USE MECMS;

CREATE TABLE USER (
    USER_ID 			VARCHAR(255) PRIMARY KEY,     			-- 사용자 고유 ID
    PROFILE_ORIGINAL	VARCHAR(255) DEFAULT "default_profile.png",                 			-- 프로필 원본 파일명 (철자 수정)
    PROFILE_UUID 		VARCHAR(255) DEFAULT "default_profile.png",                    	 	-- 프로필 이미지 UUID
    NICKNAME 			VARCHAR(255) NOT NULL UNIQUE,           -- 닉네임
    PASSWD				VARCHAR(255) NOT NULL,					-- 패스워드
    EMAIL 				VARCHAR(255) UNIQUE NOT NULL,           -- 이메일 (유니크 제약 조건)
    TEL 				VARCHAR(20),                            -- 전화번호
    GENDER 				CHAR(1) NOT NULL,             	-- 성별 (M: 남성, F: 여성)
    BIRTHDAY 			DATE 	NOT NULL,                                  	-- 생년월일
    MEMBERSHIP_TYPE 	ENUM('COMMON', 'BUSSI', 'ADMIN') NOT NULL, -- 멤버십 유형
    FOUNDING_AT 		DATE , 							-- 회원가입일
    BUSINESS_TYPE 		VARCHAR(255),                      		-- 사업 유형
    SMS_YN 				CHAR(1)   	 NOT NULL,            -- SMS 수신 여부 (N: 아니오, Y: 예)
	EMAIL_YN 			CHAR(1)   	 NOT NULL,            -- 이메일 수신 여부 (N: 아니오, Y: 예)
	JOIN_AT 			TIMESTAMP DEFAULT NOW(),   				-- 가입일
	MODIFY_AT 			TIMESTAMP DEFAULT NOW() ON UPDATE NOW(), -- 수정일
	DELETED_AT 			DATETIME DEFAULT NULL,
	STATUS 				VARCHAR(50) NOT NULL
);

CREATE TABLE STATS (
	STATS_ID		BIGINT		AUTO_INCREMENT PRIMARY KEY,		-- 통계 아이디
	CATEGORY_ID		INT 		NOT NULL, 						-- 1) 가입한 유저 통계 , 2) 탈퇴한 유저 통계 , 3) 총 유저 통계 , 4) 총 게시글 통계 , 5) 카테고리별 게시글 , 6) 댓글 통계 , 7) 방문자 통계
	STAT_DATE		DATE 		NOT NULL,  						-- 통계 기간 
	STAT_CNT		BIGINT 		DEFAULT 0,		  				-- 통계 수
	CREATED_AT		TIMESTAMP 	DEFAULT NOW(),					-- 테이블 생성시간
	UPDATED_AT		TIMESTAMP 	DEFAULT NOW() ON UPDATE NOW(),	-- 테이블 수정시간
	UNIQUE KEY UNIQUE_STAT (STAT_DATE, CATEGORY_ID)
);

CREATE TABLE VISITOR_LOG (
    VISITOR_ID      BIGINT       AUTO_INCREMENT PRIMARY KEY,  -- 방문자 로그 ID
    IP_ADDRESS      VARCHAR(45)  NOT NULL,               	  -- 클라이언트 IP (IPv4/IPv6 지원)
    VISIT_TIME      TIMESTAMP    DEFAULT NOW(),         	  -- 마지막 방문 시간
    USER_ID         VARCHAR(255) DEFAULT NULL         		  -- 로그인한 사용자 ID (NULL 가능)
);

CREATE TABLE POST_CATEGORY (
    CATEGORY_ID BIGINT PRIMARY KEY, -- 카테고리 ID
    CATEGORY_NM VARCHAR(255) NOT NULL              -- 카테고리명
);



CREATE TABLE SUB_CATEGORY (
    SUB_CATE_ID BIGINT PRIMARY KEY,  -- 말머리 ID
    SUB_CATE_NM VARCHAR(255) NOT NULL,              -- 말머리 이름
    CATEGORY_ID BIGINT NOT NULL,                    -- 카테고리 ID (연결)
    FOREIGN KEY (CATEGORY_ID) REFERENCES POST_CATEGORY(CATEGORY_ID) 
    ON DELETE CASCADE  -- 카테고리 삭제 시 해당 서브 카테고리도 삭제
);




-- 게시글 테이블
CREATE TABLE POST (
    POST_ID 			BIGINT PRIMARY KEY AUTO_INCREMENT ,  								-- 게시글 아이디
    USER_ID 			VARCHAR(255) NOT NULL,              								-- 유저 아이디
    NICKNAME			VARCHAR(255) NOT NULL, 												-- 닉네임
    CATEGORY_ID 		BIGINT NOT NULL,         											-- 카테고리 아이디 추가
   	SUB_CATE_ID			BIGINT NOT NULL,													-- 말머리 아이디 추가 
    CATEGORY_NM 		VARCHAR(255) NOT NULL,												-- 카테고리 이름 
    SUB_CATE_NM 		VARCHAR(255) NOT NULL,												-- 말머리 이름
    TITLE 				VARCHAR(255) NOT NULL,                 								-- 제목
    CONTENT 			VARCHAR(3000) NOT NULL,              								-- 본문
    VIEW_CNT 			BIGINT NOT NULL DEFAULT 0,          								-- 조회수
    STATUS 				ENUM('ACTIVE', 'DELETED') DEFAULT 'ACTIVE', 						-- 게시글 상태
    CREATED_AT         	TIMESTAMP DEFAULT NOW(), 			 								-- 작성일 (NOW() 적용)
    UPDATED_AT         	TIMESTAMP DEFAULT NOW() ON UPDATE NOW(),							-- 수정일 (자동 갱신)
    FOREIGN KEY (USER_ID) REFERENCES USER(USER_ID) ON DELETE CASCADE,  						-- 유저와 연결
    FOREIGN KEY (CATEGORY_ID) REFERENCES POST_CATEGORY(CATEGORY_ID) ON DELETE CASCADE  		-- 카테고리 삭제 시 해당 게시글도 삭제
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
    POST_ID     BIGINT NOT NULL,                     				-- 좋아요 한 게시글 아이디
    USER_ID     VARCHAR(255) NOT NULL,                				-- 유저 아이디
    CREATED_AT  TIMESTAMP DEFAULT NOW(), 							-- 좋아요 누른 시간
    FOREIGN KEY (POST_ID) REFERENCES POST(POST_ID) ON DELETE CASCADE,  	-- 게시글 연결
    FOREIGN KEY (USER_ID) REFERENCES USER(USER_ID) ON DELETE CASCADE,  	-- 유저 연결
    UNIQUE KEY (POST_ID, USER_ID) 										-- 중복 좋아요 방지
);

-- 통합 댓글 및 대댓글 좋아요 테이블
CREATE TABLE COMMENT_LIKE (
    COMMENT_ID      BIGINT NOT NULL,                                           	-- 좋아요 대상 댓글/대댓글 ID
    USER_ID         VARCHAR(255) NOT NULL,                                     	-- 좋아요를 누른 사용자 ID
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

CREATE TABLE CHAT_ROOM (
	ROOM_ID			BIGINT			AUTO_INCREMENT PRIMARY KEY,		-- 채팅방 아이디
	PRTCP_USER1		VARCHAR(255)	NOT NULL,						-- 참여자1
	PRTCP_USER2		VARCHAR(255)	NOT NULL,						-- 참여자2
	STATUS			ENUM('ACTIVE' , 'DELETED') DEFAULT 'ACTIVE', 	-- 방 상태
	CREATED_AT		TIMESTAMP 		DEFAULT CURRENT_TIMESTAMP,		-- 방 생성일
	UPDATED_AT		TIMESTAMP 		DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 방 업데이트일
	DELETED_AT		DATETIME		DEFAULT NULL,					-- 방 삭제일
	FOREIGN KEY (PRTCP_USER1) REFERENCES USER(USER_ID),
	FOREIGN KEY (PRTCP_USER2) REFERENCES USER(USER_ID)
);



CREATE TABLE CHAT_MESSAGE (
	MESSAGE_ID 		BIGINT			AUTO_INCREMENT PRIMARY KEY,		-- 메세지 아이디
	ROOM_ID			BIGINT 			NOT NULL,						-- 채팅방 아이디
	SENDER_ID		VARCHAR(255) 	NOT NULL,						-- 발신자 아이디
	RECEIVE_ID      VARCHAR(255) 	NOT NULL,						-- 수신자 아이디
	CHAT_CONTENT	TEXT 			NOT NULL,						-- 채팅 내용
	CREATED_AT		TIMESTAMP 		DEFAULT NOW(),					-- 채팅 생성 시간
	FOREIGN KEY (ROOM_ID) REFERENCES CHAT_ROOM(ROOM_ID),
	FOREIGN KEY (SENDER_ID) REFERENCES USER(USER_ID),
	FOREIGN KEY (RECEIVE_ID) REFERENCES USER(USER_ID)
);



INSERT INTO POST_CATEGORY (CATEGORY_ID, CATEGORY_NM) VALUES
(0, '공지사항'), 
(1, '외식업정보게시판'),
(2, '자유게시판'),
(3, '알바공고게시판'),
(4, '질문게시판'),
(5, '중고장비거래게시판'),
(6, '매장홍보게시판'),
(7, '협력업체게시판');

INSERT INTO SUB_CATEGORY(SUB_CATE_ID, CATEGORY_ID, SUB_CATE_NM) VALUES
(0, 0, '공지'),
(1, 2, '잡담'),
(2, 2, '일상이야기'),
(3, 2, '질문'),
(4, 1, '업계뉴스'),
(5, 1, '업체소식'),
(6, 1, '트렌드'),
(7, 1, '업계분석'),
(8, 1, '새로운 맛집'),
(9, 3, '직원 구인'),
(10, 3, '알바 구인'),
(11, 3, '구직'),
(12, 4, '업계 질문'),
(13, 4, '자유 질문'),
(14, 4, '도움 요청'),
(15, 4, '팁을 구합니다'),
(16, 5, '판매'),
(17, 5, '나눔'),
(18, 5, '교환'),
(19, 5, '구매희망'),
(20, 6, '매장 소개'),
(21, 6, '신규 오픈'),
(22, 6, '이벤트'),
(23, 6, '맛집 추천'),
(24, 7, '업체 소개'),
(25, 7, '협력 요청');