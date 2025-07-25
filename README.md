# Branch 설명
 - **basic** : 초기 세팅진행
 - **main** : 서버배포용
 - **develop** : 개발용
## Branch 사용
 - 추후기입예정

# 회원 관리 API 

Spring Boot와 MyBatis를 사용한 간단한 CRUD 회원 관리 REST API입니다.

## 기술 스택

- **언어**: Java 21
- **프레임워크**: Spring Boot 3.2.5
- **빌드 도구**: Gradle
- **데이터베이스**: MySQL 8.0
- **ORM**: MyBatis
- **API 문서화**: SpringDoc OpenAPI (Swagger UI)
- **보안**: Spring Security (BCrypt 패스워드 암호화)

## 주요 기능

- ✅ 회원 생성 (이메일/전화번호 중복 검증)
- ✅ 모든 회원 조회
- ✅ 특정 회원 조회
- ✅ 회원 정보 수정
- ✅ 회원 삭제
- ✅ 패스워드 BCrypt 암호화
- ✅ 입력 데이터 검증
- ✅ 전역 예외 처리
- ✅ Swagger UI API 문서화

## 프로젝트 구조

```
src/main/java/com/board/plan/
├── controller/          # REST API 컨트롤러
├── service/            # 비즈니스 로직
├── mapper/             # MyBatis 매퍼 인터페이스
├── model/              # 도메인 모델
├── dto/                # 데이터 전송 객체
├── exception/          # 예외 클래스들
├── config/             # 설정 클래스들
└── PlanApplication.java # 메인 애플리케이션

src/main/resources/
├── mapper/             # MyBatis XML 매퍼 파일
├── application.properties # 설정 파일
└── ...
```

## 환경 설정

### 1. MySQL 데이터베이스 설정

MySQL이 localhost:3306에서 실행 중이어야 합니다.

```bash
# MySQL에 접속
mysql -u root -p

# SQL 스크립트 실행
source database-setup.sql
```

또는 직접 SQL 실행:

```sql
-- 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS user_management_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 데이터베이스 선택
USE user_management_db;

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
```

### 2. 애플리케이션 실행

```bash
# 프로젝트 빌드 및 실행
./gradlew bootRun

# 또는 JAR 파일 생성 후 실행
./gradlew build
java -jar build/libs/plan-0.0.1-SNAPSHOT.jar
```

## API 문서

애플리케이션 실행 후 다음 URL에서 API 문서를 확인할 수 있습니다:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

## API 엔드포인트

| 메소드 | 경로 | 설명 | 상태 코드 |
|--------|------|------|-----------|
| POST | `/api/users` | 회원 생성 | 201 Created |
| GET | `/api/users` | 모든 회원 조회 | 200 OK |
| GET | `/api/users/{id}` | 특정 회원 조회 | 200 OK, 404 Not Found |
| PUT | `/api/users/{id}` | 회원 정보 수정 | 200 OK, 404 Not Found, 409 Conflict |
| DELETE | `/api/users/{id}` | 회원 삭제 | 204 No Content, 404 Not Found |

## 요청/응답 예시

### 회원 생성

**요청:**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "name": "테스트 사용자",
    "phoneNumber": "010-1111-2222"
  }'
```

**응답:**
```json
{
  "id": 1,
  "email": "test@example.com",
  "name": "테스트 사용자",
  "phoneNumber": "010-1111-2222",
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

### 모든 회원 조회

**요청:**
```bash
curl -X GET http://localhost:8080/api/users
```

**응답:**
```json
[
  {
    "id": 1,
    "email": "test@example.com",
    "name": "테스트 사용자",
    "phoneNumber": "010-1111-2222",
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
]
```

## 유효성 검증

- **이메일**: 유효한 이메일 형식 (필수)
- **비밀번호**: 필수 입력 (응답 시 제외)
- **이름**: 필수 입력
- **전화번호**: 010-1234-5678 형식 (필수, 고유)

## 오류 응답

```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "이미 존재하는 이메일입니다: test@example.com"
}
```

## 보안

- 패스워드는 BCrypt 알고리즘으로 암호화하여 저장
- API 응답에서 패스워드 필드 제외
- CSRF 보호 비활성화 (API 전용)

## 개발 환경

- **JDK**: OpenJDK 21+
- **IDE**: IntelliJ IDEA / Eclipse
- **MySQL**: 8.0+
- **Gradle**: 8.x 
