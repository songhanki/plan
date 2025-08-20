package com.board.plan.leave.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 휴가 신청 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequestDto {
    
    @NotBlank(message = "휴가 유형은 필수입니다.")
    private String leaveType;            // 휴가 유형 (연차, 병가, 경조휴가, 기타)
    
    @NotNull(message = "시작일은 필수입니다.")
    private LocalDate startDate;         // 휴가 시작일
    
    @NotNull(message = "종료일은 필수입니다.")
    private LocalDate endDate;           // 휴가 종료일
    
    private String requestReason;        // 휴가 신청 사유
}
