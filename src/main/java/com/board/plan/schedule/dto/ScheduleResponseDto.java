package com.board.plan.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 일정 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleResponseDto {
    
    private String scheduleId;           // 일정 고유 ID
    private String memberId;             // 일정을 생성한 회원 ID
    private String memberName;           // 생성자 이름
    private String title;                // 일정 제목
    private String description;          // 일정 상세 내용
    private LocalDateTime startTime;     // 일정 시작 시간
    private LocalDateTime endTime;       // 일정 종료 시간
    private Boolean allDay;              // 종일 일정 여부
    private String location;             // 일정 장소
    private String eventType;            // 일정 유형
    private String colorCode;            // 일정 표시 색상
    private Boolean isRecurring;         // 반복 일정 여부
    private String recurrenceRule;       // 반복 규칙
    private String status;               // 일정 상태
    private LocalDateTime createdAt;     // 일정 생성일
    private LocalDateTime updatedAt;     // 최종 수정일
    
    // 참여자 정보
    private List<ScheduleParticipantDto> participants;
}

