# 일정 및 휴가 관리 API 명세서

## 개요
사용자 역할 기반 권한 시스템을 구현한 일정 관리 및 휴가 관리 API입니다.

## 권한 체계
- **role-admin**: 시스템 관리자 (모든 권한)
- **role-manager**: 팀장 (팀/전사 일정 관리, 휴가 승인)  
- **role-user**: 일반 사용자 (개인 일정 관리, 휴가 신청)

## API 엔드포인트

### 1. 일정 관리 API (/api/schedules)

#### 내 일정 관리
- `GET /api/schedules/my` - 내 일정 목록 조회 (권한: role-user 이상)
- `POST /api/schedules/my` - 내 일정 등록 (권한: role-user 이상)
- `PUT /api/schedules/my/{scheduleId}` - 내 일정 수정 (권한: role-user 이상)
- `DELETE /api/schedules/my/{scheduleId}` - 내 일정 삭제 (권한: role-user 이상)

#### 팀/전사 일정 관리
- `GET /api/schedules` - 팀/전사 일정 목록 조회 (권한: role-manager 이상)
- `POST /api/schedules` - 팀/전사 일정 등록 (권한: role-manager 이상)
- `PUT /api/schedules/{scheduleId}` - 팀/전사 일정 수정 (권한: role-manager 이상)
- `DELETE /api/schedules/{scheduleId}` - 팀/전사 일정 삭제 (권한: role-manager 이상)

#### 관리자 전용
- `GET /api/schedules/all` - 모든 일정 목록 조회 (권한: role-admin)

### 2. 휴가 신청 API (/api/leaves/requests)

#### 휴가 신청 관리
- `GET /api/leaves/requests/my` - 내 휴가 신청 목록 조회 (권한: role-user 이상)
- `POST /api/leaves/requests` - 휴가 신청 (권한: role-user 이상)
- `PUT /api/leaves/requests/{requestId}` - 휴가 신청 수정 (권한: role-user 이상)
- `DELETE /api/leaves/requests/{requestId}` - 휴가 신청 취소 (권한: role-user 이상)
- `GET /api/leaves/requests/{requestId}` - 휴가 신청 상세 조회 (권한: role-user 이상)

#### 개인 휴가 이력
- `GET /api/leaves/history/my` - 내 휴가 사용 이력 조회 (권한: role-user 이상)

### 3. 휴가 관리 API (/api/leaves/approvals)

#### 휴가 승인 관리 (팀장/관리자 전용)
- `GET /api/leaves/approvals` - 휴가 신청 목록 조회 (권한: role-manager 이상)
- `PUT /api/leaves/approvals/{requestId}` - 휴가 승인/반려 (권한: role-manager 이상)

#### 휴가 이력 및 통계
- `GET /api/leaves/history` - 휴가 사용 이력 조회 (권한: role-manager 이상)
- `GET /api/leaves/statistics` - 기간별 휴가 통계 조회 (권한: role-manager 이상)

### 4. 권한 관리 API (관리자 전용)

#### 역할 관리 (/api/roles)
- `GET /api/roles` - 모든 역할 목록 조회 (권한: role-admin)
- `GET /api/roles/{roleId}` - 역할 상세 조회 (권한: role-admin)
- `POST /api/roles` - 역할 생성 (권한: role-admin)
- `PUT /api/roles/{roleId}` - 역할 수정 (권한: role-admin)
- `DELETE /api/roles/{roleId}` - 역할 삭제 (권한: role-admin)

#### 권한 관리 (/api/permissions)
- `GET /api/permissions` - 모든 권한 목록 조회 (권한: role-admin)
- `GET /api/permissions/{permissionId}` - 권한 상세 조회 (권한: role-admin)
- `POST /api/permissions` - 권한 생성 (권한: role-admin)
- `PUT /api/permissions/{permissionId}` - 권한 수정 (권한: role-admin)
- `DELETE /api/permissions/{permissionId}` - 권한 삭제 (권한: role-admin)

#### 회원 권한 조회
- `GET /api/members/{memberId}/permissions` - 특정 회원 권한 조회 (권한: role-manager 이상)
- `GET /api/my/permissions` - 내 권한 목록 조회 (권한: role-user 이상)

## 주요 DTO 구조

### ScheduleRequestDto
```json
{
  "title": "회의 제목",
  "description": "회의 설명",
  "startTime": "2024-01-01T10:00:00",
  "endTime": "2024-01-01T11:00:00",
  "allDay": false,
  "location": "회의실",
  "eventType": "MEETING",
  "colorCode": "#FF5733",
  "isRecurring": false,
  "recurrenceRule": null,
  "status": "CONFIRMED",
  "participantIds": ["user-001", "user-002"]
}
```

### LeaveRequestDto
```json
{
  "leaveType": "연차",
  "startDate": "2024-01-01",
  "endDate": "2024-01-03",
  "requestReason": "개인 휴가"
}
```

### LeaveApprovalDto
```json
{
  "approvalStatus": "승인",
  "approvalReason": "승인합니다."
}
```

## 보안 및 인증
- JWT 토큰 기반 인증
- Spring Security를 이용한 권한 기반 접근 제어
- 각 API는 @PreAuthorize를 통한 세밀한 권한 제어

## 데이터베이스 테이블
- **schedules**: 일정 정보
- **schedule_participants**: 일정 참여자
- **leave_requests**: 휴가 신청
- **leave_approvals**: 휴가 승인/반려
- **roles**: 역할 정보
- **permissions**: 권한 정보
- **role_permissions**: 역할-권한 매핑
- **members**: 회원 정보

## 기술 스택
- **Spring Boot 3.x**
- **Spring Security**
- **MyBatis**
- **MySQL**
- **JWT**
- **Swagger/OpenAPI 3**


