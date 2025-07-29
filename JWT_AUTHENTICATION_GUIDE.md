# JWT 인증 시스템 테스트 가이드

## 🚀 프로젝트 실행 방법

### 1. 데이터베이스 설정
```bash
# MySQL 접속 후 기존 스키마 사용
mysql -u root -p

# 테스트 사용자 데이터 추가
source test-users.sql
```

### 2. 프로젝트 빌드 및 실행
```bash
# Gradle을 사용한 빌드
./gradlew clean build

# Spring Boot 애플리케이션 실행
./gradlew bootRun
```

### 3. Swagger UI 접속
```
http://localhost:8080/swagger-ui.html
```

## 🔐 인증 테스트 방법

### 테스트 계정
| 역할 | 이메일 | 비밀번호 | 권한 |
|------|--------|----------|------|
| 관리자 | admin@test.com | password123 | 모든 기능 접근 |
| 팀장 | manager@test.com | password123 | 회원 조회, 휴가 승인 |
| 사용자 | user@test.com | password123 | 기본 기능만 |

### 1단계: 로그인 및 토큰 발급

**Swagger UI에서 테스트:**

1. `POST /api/auth/login` 엔드포인트 선택
2. 요청 본문에 로그인 정보 입력:
```json
{
  "email": "admin@test.com",
  "password": "password123"
}
```
3. 응답에서 `accessToken` 복사

**예상 응답:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "memberId": "user-005",
  "email": "admin@test.com",
  "name": "테스트 관리자",
  "roleId": "role-admin",
  "roleName": "관리자",
  "loginTime": "2025-01-01 12:00:00"
}
```

### 2단계: Swagger에서 JWT 토큰 설정

1. Swagger UI 상단의 **"Authorize"** 버튼 클릭
2. "Value" 필드에 다음 형식으로 입력:
```
Bearer your_access_token_here
```
3. **"Authorize"** 버튼 클릭

### 3단계: 보호된 API 테스트

#### 관리자 권한 테스트 (admin@test.com)
- ✅ `GET /api/members` - 모든 회원 조회
- ✅ `PUT /api/members/{id}` - 회원 정보 수정
- ✅ `DELETE /api/members/{id}` - 회원 삭제

#### 팀장 권한 테스트 (manager@test.com)
- ✅ `GET /api/members` - 모든 회원 조회
- ❌ `PUT /api/members/{id}` - 권한 없음 (403 Forbidden)
- ❌ `DELETE /api/members/{id}` - 권한 없음 (403 Forbidden)

#### 일반 사용자 권한 테스트 (user@test.com)
- ❌ `GET /api/members` - 권한 없음 (403 Forbidden)
- ✅ `GET /api/members/member/user-007` - 자신의 정보 조회

## 🔄 토큰 갱신 테스트

### Refresh Token 사용
1. `POST /api/auth/refresh` 엔드포인트 선택
2. 로그인 시 받은 `refreshToken` 사용:
```json
{
  "refreshToken": "your_refresh_token_here"
}
```

**예상 응답:**
```json
{
  "accessToken": "new_access_token_here",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "refreshTime": "2025-01-01 12:15:00"
}
```

## 🚨 예외 상황 테스트

### 1. 인증 실패 (401 Unauthorized)
```bash
# 잘못된 비밀번호
POST /api/auth/login
{
  "email": "admin@test.com",
  "password": "wrong_password"
}

# 응답
{
  "timestamp": "2025-01-01T12:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "이메일 또는 비밀번호가 올바르지 않습니다."
}
```

### 2. 권한 부족 (403 Forbidden)
```bash
# 일반 사용자로 관리자 기능 접근
GET /api/members
Authorization: Bearer user_token

# 응답
{
  "timestamp": "2025-01-01T12:00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "해당 리소스에 접근할 권한이 없습니다."
}
```

### 3. 토큰 없음 (401 Unauthorized)
```bash
# Authorization 헤더 없이 보호된 API 호출
GET /api/members

# 응답
{
  "error": "Unauthorized",
  "message": "인증이 필요합니다. 로그인 후 다시 시도해주세요.",
  "timestamp": "1672531200000"
}
```

## 🔧 디버깅 팁

### 1. 토큰 만료 시
- Access Token은 15분 후 만료
- 만료된 토큰 사용 시 401 오류 발생
- Refresh Token을 사용하여 새 토큰 발급

### 2. 권한 확인
- JWT 토큰의 `roleId` 클레임 확인
- Spring Security 로그 레벨을 DEBUG로 설정:
```properties
logging.level.org.springframework.security=DEBUG
```

### 3. 토큰 검증
- `GET /api/auth/validate` 엔드포인트로 토큰 유효성 확인
- 응답에서 토큰 상태 확인

## 🌐 CORS 설정

현재 개발 환경용으로 모든 오리진 허용:
```java
configuration.setAllowedOriginPatterns(Arrays.asList("*"));
```

**운영 환경에서는 특정 도메인만 허용하도록 변경 필요!**

## 🔒 보안 고려사항

### JWT Secret Key
- **중요**: 운영 환경에서는 환경 변수로 관리
```bash
export JWT_SECRET=your_very_secure_secret_key_here
```

### 토큰 저장
- **클라이언트**: localStorage 대신 httpOnly 쿠키 권장
- **서버**: Refresh Token 블랙리스트 구현 고려

### 로그아웃 처리
- 현재는 클라이언트에서 토큰 삭제
- 향후 Redis 기반 토큰 블랙리스트 구현 예정

## 📝 추가 개발 예정 기능

1. **토큰 블랙리스트**: Redis를 사용한 로그아웃 토큰 무효화
2. **권한 세분화**: 더 세밀한 권한 관리
3. **로그인 이력**: 사용자별 로그인 기록 관리
4. **계정 잠금**: 로그인 실패 시 계정 임시 잠금 