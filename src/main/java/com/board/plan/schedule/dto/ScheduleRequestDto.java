package com.board.plan.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 일정 생성/수정 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleRequestDto {
    
    @NotBlank(message = "일정 제목은 필수입니다.")
    private String title;                // 일정 제목
    
    private String description;          // 일정 상세 내용
    
    @NotNull(message = "시작 시간은 필수입니다.")
    private LocalDateTime startTime;     // 일정 시작 시간
    
    @NotNull(message = "종료 시간은 필수입니다.")
    private LocalDateTime endTime;       // 일정 종료 시간
    
    @Builder.Default
    private Boolean allDay = false;      // 종일 일정 여부
    private String location;             // 일정 장소
    @Builder.Default
    private String eventType = "PERSONAL"; // 일정 유형
    private String colorCode;            // 일정 표시 색상
    @Builder.Default
    private Boolean isRecurring = false; // 반복 일정 여부
    private String recurrenceRule;       // 반복 규칙
    @Builder.Default
    private String status = "CONFIRMED"; // 일정 상태
    
    // 팀/전사 일정용 참여자 ID 목록
    private List<String> participantIds;
}
