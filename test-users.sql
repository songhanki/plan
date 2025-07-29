-- JWT 인증 테스트용 사용자 데이터
-- 비밀번호: 모두 'password123'으로 통일 (BCrypt로 해시됨)

USE planer_db;

-- 기존 테스트 데이터 삭제 (필요시)
-- DELETE FROM members WHERE member_id IN ('user-005', 'user-006', 'user-007');

-- 테스트용 사용자 추가
INSERT INTO members (
    member_id, email, password, name, nickname, phone_number, 
    gender, date_of_birth, status, role_id, 
    created_at, updated_at
) VALUES 
-- 관리자 계정
(
    'user-005', 
    'admin@test.com', 
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 
    '테스트 관리자', 
    '관리자', 
    '010-0001-0001', 
    'M', 
    '1980-01-01', 
    'ACTIVE', 
    'role-admin', 
    NOW(), 
    NOW()
),
-- 팀장 계정
(
    'user-006', 
    'manager@test.com', 
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 
    '테스트 팀장', 
    '팀장', 
    '010-0002-0002', 
    'F', 
    '1985-05-15', 
    'ACTIVE', 
    'role-manager', 
    NOW(), 
    NOW()
),
-- 일반 사용자 계정
(
    'user-007', 
    'user@test.com', 
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 
    '테스트 사용자', 
    '사용자', 
    '010-0003-0003', 
    'M', 
    '1990-12-25', 
    'ACTIVE', 
    'role-user', 
    NOW(), 
    NOW()
);

-- 생성된 사용자 확인
SELECT 
    member_id, 
    email, 
    name, 
    role_id, 
    status,
    created_at
FROM members 
WHERE member_id IN ('user-005', 'user-006', 'user-007')
ORDER BY member_id;

-- 로그인 테스트 계정 정보
-- ================================
-- 관리자: admin@test.com / password123
-- 팀장:   manager@test.com / password123  
-- 사용자: user@test.com / password123
-- ================================ 