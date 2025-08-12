-- planer_db 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS planer_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 데이터베이스 선택
USE planer_db;

-- users 테이블 생성
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_phone_number (phone_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 샘플 데이터 삽입 (비밀번호는 'password123'을 SHA256로 암호화한 값)
INSERT INTO users (email, password, name, phone_number) VALUES 
('admin@example.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', '관리자', '010-1234-5678'),
('user1@example.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', '사용자1', '010-9876-5432');

-- 테이블 구조 확인
DESCRIBE users; 

--- 권한 관리 테이블 ---
-- 역할 정보 테이블
CREATE TABLE roles ( 
	role_id VARCHAR(36) PRIMARY KEY NOT NULL COMMENT '역할 고유 ID', 
	role_name VARCHAR(100) NOT NULL UNIQUE COMMENT '역할 이름', description TEXT COMMENT '역할 설명', 
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일', 
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정일'
);
-- 권한 정보 테이블
CREATE TABLE permissions ( 
	permission_id VARCHAR(36) PRIMARY KEY NOT NULL COMMENT '권한 고유 ID', 
	permission_name VARCHAR(100) NOT NULL UNIQUE COMMENT '권한 이름', 
	description TEXT COMMENT '권한 설명', 
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일', 
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정일'
);
-- 역할-권한 매핑 테이블
CREATE TABLE role_permissions ( 
	role_permission_id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT '매핑 고유 ID', 
	role_id VARCHAR(36) NOT NULL COMMENT '역할 ID', permission_id VARCHAR(36) NOT NULL COMMENT '권한 ID', 
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일', 
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정일', 
	FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE, 
	FOREIGN KEY (permission_id) REFERENCES permissions(permission_id) ON DELETE CASCADE, UNIQUE (role_id, permission_id) -- 중복 매핑 방지
);
-- --- 회원 관리 테이블 (role_id 추가) ---
-- 회원 정보 테이블
CREATE TABLE members ( 
	member_id VARCHAR(36) PRIMARY KEY NOT NULL COMMENT '회원 고유 ID (UUID) (user-XXX 형식)', 
	email VARCHAR(255) NOT NULL UNIQUE COMMENT '회원 이메일 (로그인 ID)', 
	password VARCHAR(255) NOT NULL COMMENT '해시된 비밀번호', 
	name VARCHAR(100) NOT NULL COMMENT '회원 이름', 
	nickname VARCHAR(100) UNIQUE COMMENT '회원 닉네임 (선택 사항)', 
	phone_number VARCHAR(20) UNIQUE COMMENT '회원 전화번호 (하이픈 제외)', 
	gender ENUM('M', 'F', 'O') COMMENT '성별 (남성: M, 여성: F, 기타: O)', 
	date_of_birth DATE COMMENT '생년월일', 
	profile_image_url VARCHAR(500) COMMENT '프로필 이미지 URL', 
	status ENUM('ACTIVE', 'INACTIVE', 'BLOCKED', 'DELETED') NOT NULL DEFAULT 'ACTIVE' COMMENT '회원 상태', 
	role_id VARCHAR(36) NOT NULL COMMENT '할당된 역할 ID', 
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '계정 생성일', 
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정일', 
	last_login_at DATETIME COMMENT '최종 로그인 일시', 
	FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE RESTRICT -- 역할 삭제 시 회원이 존재하면 삭제 불가
);
-- 회원 주소 정보 테이블 (기존과 동일)
CREATE TABLE member_addresses ( 
	address_id VARCHAR(36) PRIMARY KEY NOT NULL COMMENT '주소 고유 ID (UUID)', 
	member_id VARCHAR(36) NOT NULL COMMENT '회원 ID (외래키)', 
	address_type ENUM('HOME', 'WORK', 'OTHER') NOT NULL COMMENT '주소 유형 (집, 직장, 기타)', 
	postal_code VARCHAR(10) NOT NULL COMMENT '우편번호', 
	address_main VARCHAR(255) NOT NULL COMMENT '기본 주소', 
	address_detail VARCHAR(255) COMMENT '상세 주소', 
	is_default BOOLEAN NOT NULL DEFAULT FALSE COMMENT '기본 주소 여부', 
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일', 
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정일', 
	FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE
);
-- 회원 로그인 기록 테이블 (기존과 동일)
CREATE TABLE member_login_logs ( 
	log_id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT '로그 고유 ID', 
	member_id VARCHAR(36) NOT NULL COMMENT '회원 ID (외래키)', 
	login_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '로그인 시각', 
	ip_address VARCHAR(45) NOT NULL COMMENT '로그인 IP 주소', 
	user_agent TEXT COMMENT '사용자 에이전트 정보', 
	login_success BOOLEAN NOT NULL COMMENT '로그인 성공 여부', 
	failure_reason VARCHAR(255) COMMENT '로그인 실패 사유', 
	FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE
);
-- --- 일정 관리 테이블 (기존과 동일) ---
-- 일정 정보 테이블
CREATE TABLE schedules ( 
	schedule_id VARCHAR(36) PRIMARY KEY NOT NULL COMMENT '일정 고유 ID (UUID)', 
	member_id VARCHAR(36) NOT NULL COMMENT '일정을 생성한 회원 ID (외래키)', 
	title VARCHAR(255) NOT NULL COMMENT '일정 제목', 
	description TEXT COMMENT '일정 상세 내용', 
	start_time DATETIME NOT NULL COMMENT '일정 시작 시간', 
	end_time DATETIME NOT NULL COMMENT '일정 종료 시간', 
	all_day BOOLEAN NOT NULL DEFAULT FALSE COMMENT '종일 일정 여부', 
	location VARCHAR(255) COMMENT '일정 장소', 
	event_type ENUM('MEETING', 'TASK', 'PERSONAL', 'HOLIDAY', 'OTHER') NOT NULL DEFAULT 'PERSONAL' COMMENT '일정 유형', 
	color_code VARCHAR(7) COMMENT '일정 표시 색상 (Hex 코드)', 
	is_recurring BOOLEAN NOT NULL DEFAULT FALSE COMMENT '반복 일정 여부', 
	recurrence_rule VARCHAR(255) COMMENT '반복 규칙 (예: RRule 형식)', 
	status ENUM('CONFIRMED', 'CANCELED', 'PENDING') NOT NULL DEFAULT 'CONFIRMED' COMMENT '일정 상태', 
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '일정 생성일', 
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정일', 
	FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE
);
-- 일정 참여자 정보 테이블
CREATE TABLE schedule_participants ( 
	participant_id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT '참여자 기록 고유 ID', 
	schedule_id VARCHAR(36) NOT NULL COMMENT '일정 ID (외래키)', 
	member_id VARCHAR(36) NOT NULL COMMENT '참여 회원 ID (외래키)', 
	participation_status ENUM('ATTENDING', 'DECLINED', 'TENTATIVE') NOT NULL DEFAULT 'ATTENDING' COMMENT '일정 참여 상태 (참석, 거절, 미정)', 
	invited_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '초대 일시', 
	responded_at DATETIME COMMENT '응답 일시', 
	is_organizer BOOLEAN NOT NULL DEFAULT FALSE COMMENT '일정 주최자 여부 (생성자와 동일)', 
	FOREIGN KEY (schedule_id) REFERENCES schedules(schedule_id) ON DELETE CASCADE, FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE, UNIQUE (schedule_id, member_id)
);
-- --- 휴가 관리 테이블 (기존과 동일) ---
-- 휴가 신청 정보 테이블
CREATE TABLE leave_requests ( 
	request_id VARCHAR(36) PRIMARY KEY NOT NULL COMMENT '휴가 신청 고유 ID (UUID)', 
	member_id VARCHAR(36) NOT NULL COMMENT '신청한 회원 ID (외래키)', 
	leave_type ENUM('연차', '병가', '경조휴가', '기타') NOT NULL COMMENT '휴가 유형', 
	start_date DATE NOT NULL COMMENT '휴가 시작일', 
	end_date DATE NOT NULL COMMENT '휴가 종료일', 
	request_reason TEXT COMMENT '휴가 신청 사유', 
	status ENUM('신청', '승인', '반려', '취소') NOT NULL DEFAULT '신청' COMMENT '휴가 신청 상태', 
	request_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '휴가 신청일시', 
	approval_id VARCHAR(36) COMMENT '관련 승인/반려 정보 ID (외래키, 선택 사항)', 
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일', 
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정일', 
	FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE -- approval_id는 leave_approvals 테이블이 먼저 생성된 후에 제약 조건을 추가할 수 있습니다.
);
-- 휴가 승인/반려 정보 테이블
CREATE TABLE leave_approvals ( 
	approval_id VARCHAR(36) PRIMARY KEY NOT NULL COMMENT '승인/반려 기록 고유 ID (UUID)', 
	request_id VARCHAR(36) NOT NULL UNIQUE COMMENT '관련 휴가 신청 ID (외래키) - 하나의 신청에 하나의 승인/반려', 
	approver_id VARCHAR(36) NOT NULL COMMENT '승인/반려한 회원 ID (외래키)', 
	approval_status ENUM('승인', '반려') NOT NULL COMMENT '승인/반려 상태', 
	approval_reason TEXT COMMENT '반려 사유 (반려 시 필수)', 
	approval_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '승인/반려 일시', 
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일', 
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정일', 
	FOREIGN KEY (request_id) REFERENCES leave_requests(request_id) ON DELETE CASCADE, FOREIGN KEY (approver_id) REFERENCES members(member_id) ON DELETE CASCADE
);
-- leave_requests 테이블의 approval_id 외래키 추가 (leave_approvals 테이블 생성 후)
ALTER TABLE leave_requests
ADD CONSTRAINT fk_approval_id
FOREIGN KEY (approval_id) REFERENCES leave_approvals(approval_id) ON DELETE SET NULL;

-- roles 테이블 데이터
INSERT INTO roles (role_id, role_name, description, created_at, updated_at) VALUES
('role-admin', '관리자', '시스템의 모든 기능을 관리', NOW(), NOW()),
('role-manager', '팀장', '팀원 관리 및 휴가 승인 권한', NOW(), NOW()),
('role-user', '일반 사용자', '자신의 정보 및 일정 관리, 휴가 신청', NOW(), NOW());

-- permissions 테이블 데이터
INSERT INTO permissions (permission_id, permission_name, description, created_at, updated_at) VALUES
('perm-member-read', '회원 조회', '회원 정보 조회', NOW(), NOW()),
('perm-member-create', '회원 생성', '새로운 회원 계정 생성', NOW(), NOW()),
('perm-member-update', '회원 수정', '회원 정보 수정', NOW(), NOW()),
('perm-member-delete', '회원 삭제', '회원 계정 삭제', NOW(), NOW()),
('perm-schedule-create', '일정 생성', '개인/공유 일정 생성', NOW(), NOW()),
('perm-schedule-read-own', '내 일정 조회', '자신이 생성하거나 참여한 일정 조회', NOW(), NOW()),
('perm-schedule-read-all', '모든 일정 조회', '모든 사용자 일정 조회', NOW(), NOW()),
('perm-leave-request', '휴가 신청', '휴가 신청 기능 사용', NOW(), NOW()),
('perm-leave-approve', '휴가 승인', '직원 휴가 신청 승인/반려', NOW(), NOW());

-- role_permissions 테이블 데이터
-- 관리자 역할에 모든 권한 부여
INSERT INTO role_permissions (role_id, permission_id, created_at, updated_at) VALUES
('role-admin', 'perm-member-read', NOW(), NOW()),
('role-admin', 'perm-member-create', NOW(), NOW()),
('role-admin', 'perm-member-update', NOW(), NOW()),
('role-admin', 'perm-member-delete', NOW(), NOW()),
('role-admin', 'perm-schedule-create', NOW(), NOW()),
('role-admin', 'perm-schedule-read-all', NOW(), NOW()),
('role-admin', 'perm-leave-request', NOW(), NOW()),
('role-admin', 'perm-leave-approve', NOW(), NOW());

-- 팀장 역할에 일부 관리 및 휴가 승인 권한 부여
INSERT INTO role_permissions (role_id, permission_id, created_at, updated_at) VALUES
('role-manager', 'perm-member-read', NOW(), NOW()),
('role-manager', 'perm-schedule-create', NOW(), NOW()),
('role-manager', 'perm-schedule-read-all', NOW(), NOW()),
('role-manager', 'perm-leave-request', NOW(), NOW()),
('role-manager', 'perm-leave-approve', NOW(), NOW());

-- 일반 사용자 역할에 기본 권한 부여
INSERT INTO role_permissions (role_id, permission_id, created_at, updated_at) VALUES
('role-user', 'perm-schedule-create', NOW(), NOW()),
('role-user', 'perm-schedule-read-own', NOW(), NOW()),
('role-user', 'perm-leave-request', NOW(), NOW());

-- members 테이블 데이터
-- 비밀번호는 'password'를 SHA256로 해싱했다고 가정 (실제로는 애플리케이션에서 해싱)
-- member_id는 UUID를 사용하여 임의의 값을 생성
INSERT INTO members (member_id, email, password, name, nickname, phone_number, gender, date_of_birth, profile_image_url, status, role_id, created_at, updated_at, last_login_at) VALUES
('user-001', 'admin@example.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', '관리자', '시스템관리자', '01011112222', 'M', '1980-01-01', NULL, 'ACTIVE', 'role-admin', NOW(), NOW(), NOW()),
('user-002', 'manager@example.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', '김팀장', '팀장님', '01033334444', 'F', '1985-05-10', NULL, 'ACTIVE', 'role-manager', NOW(), NOW(), NOW()),
('user-003', 'user1@example.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', '이사용자', '사용자1', '01055556666', 'M', '1990-11-20', NULL, 'ACTIVE', 'role-user', NOW(), NOW(), NOW()),
('user-004', 'user2@example.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', '박사원', '사원2', '01077778888', 'F', '1992-03-15', NULL, 'ACTIVE', 'role-user', NOW(), NOW(), NOW());

-- member_addresses 테이블 데이터
INSERT INTO member_addresses (address_id, member_id, address_type, postal_code, address_main, address_detail, is_default, created_at, updated_at) VALUES
('addr-001', 'user-003', 'HOME', '06130', '서울 강남구 테헤란로 123', '아크로빌딩 101호', TRUE, NOW(), NOW()),
('addr-002', 'user-003', 'WORK', '08503', '서울 금천구 가산디지털1로 100', 'B동 502호', FALSE, NOW(), NOW()),
('addr-003', 'user-002', 'HOME', '03030', '서울 종로구 삼일대로 10', '하늘채 아파트 101동 101호', TRUE, NOW(), NOW());

-- member_login_logs 테이블 데이터
INSERT INTO member_login_logs (member_id, login_at, ip_address, user_agent, login_success, failure_reason) VALUES
('user-001', NOW(), '192.168.1.10', 'Mozilla/5.0 ... (Chrome)', TRUE, NULL),
('user-003', NOW() - INTERVAL 1 HOUR, '10.0.0.5', 'Mozilla/5.0 ... (Firefox)', TRUE, NULL),
('user-003', NOW() - INTERVAL 30 MINUTE, '10.0.0.5', 'Mobile Safari', FALSE, 'Incorrect password'),
('user-002', NOW() - INTERVAL 2 HOUR, '172.16.0.20', 'Edge', TRUE, NULL);

-- schedules 테이블 데이터
INSERT INTO schedules (schedule_id, member_id, title, description, start_time, end_time, all_day, location, event_type, color_code, is_recurring, recurrence_rule, status, created_at, updated_at) VALUES
('sched-001', 'user-001', '주간 업무 보고 회의', '각 팀별 주간 업무 보고 및 다음 주 계획 논의', '2025-08-01 10:00:00', '2025-08-01 11:00:00', FALSE, '본사 대회의실', 'MEETING', '#FF5733', FALSE, NULL, 'CONFIRMED', NOW(), NOW()),
('sched-002', 'user-003', '개인 건강 검진', '매년 정기 건강 검진', '2025-08-05 09:00:00', '2025-08-05 12:00:00', FALSE, '강남 건강검진센터', 'PERSONAL', '#33FF57', FALSE, NULL, 'CONFIRMED', NOW(), NOW()),
('sched-003', 'user-002', '신제품 아이디어 브레인스토밍', '새로운 제품 아이디어 도출', '2025-08-10 14:00:00', '2025-08-10 16:00:00', FALSE, '온라인 (Zoom)', 'MEETING', '#3357FF', FALSE, NULL, 'CONFIRMED', NOW(), NOW());

-- schedule_participants 테이블 데이터
INSERT INTO schedule_participants (schedule_id, member_id, participation_status, invited_at, responded_at, is_organizer) VALUES
('sched-001', 'user-001', 'ATTENDING', NOW(), NOW(), TRUE),
('sched-001', 'user-002', 'ATTENDING', NOW(), NOW(), FALSE),
('sched-001', 'user-003', 'ATTENDING', NOW(), NOW(), FALSE),
('sched-003', 'user-002', 'ATTENDING', NOW(), NOW(), TRUE),
('sched-003', 'user-003', 'ATTENDING', NOW(), NOW(), FALSE),
('sched-003', 'user-004', 'DECLINED', NOW(), NOW() + INTERVAL 5 MINUTE, FALSE);

-- leave_requests 테이블 데이터 (승인 전 상태)
INSERT INTO leave_requests (request_id, member_id, leave_type, start_date, end_date, request_reason, status, request_date, created_at, updated_at) VALUES
('req-001', 'user-003', '연차', '2025-08-20', '2025-08-22', '개인 연차 사용', '신청', NOW(), NOW(), NOW()),
('req-002', 'user-004', '병가', '2025-08-25', '2025-08-25', '독감 증상으로 인한 병가', '신청', NOW(), NOW(), NOW()),
('req-003', 'user-003', '경조휴가', '2025-09-01', '2025-09-03', '가족 결혼식 참석', '신청', NOW(), NOW(), NOW());

-- leave_approvals 테이블 데이터 (승인/반려 상태)
INSERT INTO leave_approvals (approval_id, request_id, approver_id, approval_status, approval_reason, approval_date, created_at, updated_at) VALUES
('app-001', 'req-001', 'user-002', '승인', '승인되었습니다.', NOW(), NOW(), NOW()),
('app-002', 'req-002', 'user-002', '반려', '업무 공백이 예상되어 반려합니다. 일정 조정 후 재신청 바랍니다.', NOW(), NOW(), NOW());

-- leave_requests 테이블 업데이트 (approval_id 참조)
UPDATE leave_requests SET approval_id = 'app-001', status = '승인' WHERE request_id = 'req-001';
UPDATE leave_requests SET approval_id = 'app-002', status = '반려' WHERE request_id = 'req-002';

-- Database에 있는 테이블 전체 삭제
SET FOREIGN_KEY_CHECKS = 0;
SET @tables = NULL;
SELECT GROUP_CONCAT(table_schema, '.', table_name) INTO @tables
    FROM information_schema.tables
    WHERE table_schema = 'planer_db'; -- specify DB name here.
SET @tables = CONCAT('DROP TABLE ', @tables);
PREPARE stmt FROM @tables;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

CREATE TABLE tokens (
    member_id VARCHAR(36) NOT NULL,
    access_token TEXT NOT NULL,
    refresh_token TEXT NOT NULL,
    access_token_expires_at DATETIME NOT NULL,
    refresh_token_expires_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    PRIMARY KEY (member_id),
    CONSTRAINT fk_tokens_member FOREIGN KEY (member_id) REFERENCES members(member_id)
);

CREATE INDEX idx_tokens_access_token ON tokens (access_token(100));