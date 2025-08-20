package com.board.plan.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 일정 참여자 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleParticipantDto {
    
    private Long participantId;                  // 참여자 기록 고유 ID
    private String scheduleId;                   // 일정 ID
    private String memberId;                     // 참여 회원 ID
    private String memberName;                   // 참여 회원 이름
    private String participationStatus;          // 일정 참여 상태
    private LocalDateTime invitedAt;             // 초대 일시
    private LocalDateTime respondedAt;           // 응답 일시
    private Boolean isOrganizer;                 // 일정 주최자 여부
}

