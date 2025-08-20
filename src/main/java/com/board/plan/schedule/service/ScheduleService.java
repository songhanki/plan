package com.board.plan.schedule.service;

import com.board.plan.core.exception.UserNotFoundException;
import com.board.plan.schedule.dto.ScheduleRequestDto;
import com.board.plan.schedule.dto.ScheduleResponseDto;
import com.board.plan.schedule.dto.ScheduleParticipantDto;
import com.board.plan.schedule.mapper.ScheduleMapper;
import com.board.plan.schedule.model.Schedule;
import com.board.plan.schedule.model.ScheduleParticipant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 일정 관리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleMapper scheduleMapper;

    /**
     * 내 일정 목록 조회
     */
    public List<ScheduleResponseDto> getMySchedules(String memberId, LocalDate startDate, LocalDate endDate) {
        log.info("조회 중인 회원 일정: memberId={}, 시작일={}, 종료일={}", memberId, startDate, endDate);
        
        List<Schedule> schedules = scheduleMapper.selectMySchedules(memberId, startDate, endDate);
        return schedules.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 팀/전사 일정 목록 조회
     */
    public List<ScheduleResponseDto> getTeamSchedules(LocalDate startDate, LocalDate endDate) {
        log.info("팀/전사 일정 조회: 시작일={}, 종료일={}", startDate, endDate);
        
        List<Schedule> schedules = scheduleMapper.selectTeamSchedules(startDate, endDate);
        return schedules.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 모든 일정 목록 조회 (관리자용)
     */
    public List<ScheduleResponseDto> getAllSchedules(LocalDate startDate, LocalDate endDate) {
        log.info("모든 일정 조회: 시작일={}, 종료일={}", startDate, endDate);
        
        List<Schedule> schedules = scheduleMapper.selectAllSchedules(startDate, endDate);
        return schedules.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 내 일정 생성
     */
    @Transactional
    public ScheduleResponseDto createMySchedule(String memberId, ScheduleRequestDto requestDto) {
        log.info("개인 일정 생성: memberId={}, 제목={}", memberId, requestDto.getTitle());
        
        validateScheduleRequest(requestDto);
        
        String scheduleId = "sched-" + UUID.randomUUID().toString();
        
        Schedule schedule = Schedule.builder()
                .scheduleId(scheduleId)
                .memberId(memberId)
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .startTime(requestDto.getStartTime())
                .endTime(requestDto.getEndTime())
                .allDay(requestDto.getAllDay())
                .location(requestDto.getLocation())
                .eventType(requestDto.getEventType())
                .colorCode(requestDto.getColorCode())
                .isRecurring(requestDto.getIsRecurring())
                .recurrenceRule(requestDto.getRecurrenceRule())
                .status(requestDto.getStatus())
                .build();

        scheduleMapper.insertSchedule(schedule);

        // 본인을 주최자로 추가
        ScheduleParticipant organizer = ScheduleParticipant.builder()
                .scheduleId(scheduleId)
                .memberId(memberId)
                .participationStatus("ATTENDING")
                .isOrganizer(true)
                .build();
        
        scheduleMapper.insertScheduleParticipant(organizer);

        return getScheduleById(scheduleId);
    }

    /**
     * 팀/전사 일정 생성
     */
    @Transactional
    public ScheduleResponseDto createTeamSchedule(String organizerId, ScheduleRequestDto requestDto) {
        log.info("팀/전사 일정 생성: organizerId={}, 제목={}", organizerId, requestDto.getTitle());
        
        validateScheduleRequest(requestDto);
        
        String scheduleId = "sched-" + UUID.randomUUID().toString();
        
        Schedule schedule = Schedule.builder()
                .scheduleId(scheduleId)
                .memberId(organizerId)
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .startTime(requestDto.getStartTime())
                .endTime(requestDto.getEndTime())
                .allDay(requestDto.getAllDay())
                .location(requestDto.getLocation())
                .eventType(requestDto.getEventType())
                .colorCode(requestDto.getColorCode())
                .isRecurring(requestDto.getIsRecurring())
                .recurrenceRule(requestDto.getRecurrenceRule())
                .status(requestDto.getStatus())
                .build();

        scheduleMapper.insertSchedule(schedule);

        // 주최자 추가
        ScheduleParticipant organizer = ScheduleParticipant.builder()
                .scheduleId(scheduleId)
                .memberId(organizerId)
                .participationStatus("ATTENDING")
                .isOrganizer(true)
                .build();
        
        scheduleMapper.insertScheduleParticipant(organizer);

        // 참여자들 추가
        if (requestDto.getParticipantIds() != null && !requestDto.getParticipantIds().isEmpty()) {
            for (String participantId : requestDto.getParticipantIds()) {
                if (!participantId.equals(organizerId)) { // 주최자 중복 방지
                    ScheduleParticipant participant = ScheduleParticipant.builder()
                            .scheduleId(scheduleId)
                            .memberId(participantId)
                            .participationStatus("ATTENDING")
                            .isOrganizer(false)
                            .build();
                    
                    scheduleMapper.insertScheduleParticipant(participant);
                }
            }
        }

        return getScheduleById(scheduleId);
    }

    /**
     * 내 일정 수정
     */
    @Transactional
    public ScheduleResponseDto updateMySchedule(String memberId, String scheduleId, ScheduleRequestDto requestDto) {
        log.info("개인 일정 수정: memberId={}, scheduleId={}", memberId, scheduleId);
        
        // 소유자 확인
        if (!scheduleMapper.isScheduleOwner(scheduleId, memberId)) {
            throw new UserNotFoundException("일정을 수정할 권한이 없습니다.");
        }
        
        validateScheduleRequest(requestDto);
        
        Schedule existingSchedule = scheduleMapper.selectScheduleById(scheduleId);
        if (existingSchedule == null) {
            throw new UserNotFoundException("일정을 찾을 수 없습니다.");
        }

        Schedule schedule = Schedule.builder()
                .scheduleId(scheduleId)
                .memberId(memberId)
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .startTime(requestDto.getStartTime())
                .endTime(requestDto.getEndTime())
                .allDay(requestDto.getAllDay())
                .location(requestDto.getLocation())
                .eventType(requestDto.getEventType())
                .colorCode(requestDto.getColorCode())
                .isRecurring(requestDto.getIsRecurring())
                .recurrenceRule(requestDto.getRecurrenceRule())
                .status(requestDto.getStatus())
                .build();

        scheduleMapper.updateSchedule(schedule);
        return getScheduleById(scheduleId);
    }

    /**
     * 팀/전사 일정 수정
     */
    @Transactional
    public ScheduleResponseDto updateTeamSchedule(String managerId, String scheduleId, ScheduleRequestDto requestDto) {
        log.info("팀/전사 일정 수정: managerId={}, scheduleId={}", managerId, scheduleId);
        
        validateScheduleRequest(requestDto);
        
        Schedule existingSchedule = scheduleMapper.selectScheduleById(scheduleId);
        if (existingSchedule == null) {
            throw new UserNotFoundException("일정을 찾을 수 없습니다.");
        }

        Schedule schedule = Schedule.builder()
                .scheduleId(scheduleId)
                .memberId(existingSchedule.getMemberId()) // 원래 생성자 유지
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .startTime(requestDto.getStartTime())
                .endTime(requestDto.getEndTime())
                .allDay(requestDto.getAllDay())
                .location(requestDto.getLocation())
                .eventType(requestDto.getEventType())
                .colorCode(requestDto.getColorCode())
                .isRecurring(requestDto.getIsRecurring())
                .recurrenceRule(requestDto.getRecurrenceRule())
                .status(requestDto.getStatus())
                .build();

        scheduleMapper.updateSchedule(schedule);

        // 참여자 업데이트 (기존 참여자 삭제 후 재추가)
        if (requestDto.getParticipantIds() != null) {
            scheduleMapper.deleteScheduleParticipants(scheduleId);
            
            // 주최자 다시 추가
            ScheduleParticipant organizer = ScheduleParticipant.builder()
                    .scheduleId(scheduleId)
                    .memberId(existingSchedule.getMemberId())
                    .participationStatus("ATTENDING")
                    .isOrganizer(true)
                    .build();
            
            scheduleMapper.insertScheduleParticipant(organizer);
            
            // 새로운 참여자들 추가
            for (String participantId : requestDto.getParticipantIds()) {
                if (!participantId.equals(existingSchedule.getMemberId())) {
                    ScheduleParticipant participant = ScheduleParticipant.builder()
                            .scheduleId(scheduleId)
                            .memberId(participantId)
                            .participationStatus("ATTENDING")
                            .isOrganizer(false)
                            .build();
                    
                    scheduleMapper.insertScheduleParticipant(participant);
                }
            }
        }

        return getScheduleById(scheduleId);
    }

    /**
     * 내 일정 삭제
     */
    @Transactional
    public void deleteMySchedule(String memberId, String scheduleId) {
        log.info("개인 일정 삭제: memberId={}, scheduleId={}", memberId, scheduleId);
        
        // 소유자 확인
        if (!scheduleMapper.isScheduleOwner(scheduleId, memberId)) {
            throw new UserNotFoundException("일정을 삭제할 권한이 없습니다.");
        }
        
        scheduleMapper.deleteSchedule(scheduleId);
    }

    /**
     * 팀/전사 일정 삭제
     */
    @Transactional
    public void deleteTeamSchedule(String managerId, String scheduleId) {
        log.info("팀/전사 일정 삭제: managerId={}, scheduleId={}", managerId, scheduleId);
        
        Schedule existingSchedule = scheduleMapper.selectScheduleById(scheduleId);
        if (existingSchedule == null) {
            throw new UserNotFoundException("일정을 찾을 수 없습니다.");
        }
        
        scheduleMapper.deleteSchedule(scheduleId);
    }

    /**
     * 일정 조회 (ID로)
     */
    public ScheduleResponseDto getScheduleById(String scheduleId) {
        Schedule schedule = scheduleMapper.selectScheduleById(scheduleId);
        if (schedule == null) {
            throw new UserNotFoundException("일정을 찾을 수 없습니다.");
        }
        
        return convertToResponseDto(schedule);
    }

    /**
     * Schedule을 ScheduleResponseDto로 변환
     */
    private ScheduleResponseDto convertToResponseDto(Schedule schedule) {
        List<ScheduleParticipant> participants = scheduleMapper.selectScheduleParticipants(schedule.getScheduleId());
        
        List<ScheduleParticipantDto> participantDtos = participants.stream()
                .map(participant -> ScheduleParticipantDto.builder()
                        .participantId(participant.getParticipantId())
                        .scheduleId(participant.getScheduleId())
                        .memberId(participant.getMemberId())
                        .participationStatus(participant.getParticipationStatus())
                        .invitedAt(participant.getInvitedAt())
                        .respondedAt(participant.getRespondedAt())
                        .isOrganizer(participant.getIsOrganizer())
                        .build())
                .collect(Collectors.toList());

        return ScheduleResponseDto.builder()
                .scheduleId(schedule.getScheduleId())
                .memberId(schedule.getMemberId())
                .title(schedule.getTitle())
                .description(schedule.getDescription())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .allDay(schedule.getAllDay())
                .location(schedule.getLocation())
                .eventType(schedule.getEventType())
                .colorCode(schedule.getColorCode())
                .isRecurring(schedule.getIsRecurring())
                .recurrenceRule(schedule.getRecurrenceRule())
                .status(schedule.getStatus())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .participants(participantDtos)
                .build();
    }

    /**
     * 일정 요청 유효성 검증
     */
    private void validateScheduleRequest(ScheduleRequestDto requestDto) {
        if (requestDto.getStartTime().isAfter(requestDto.getEndTime())) {
            throw new IllegalArgumentException("시작 시간이 종료 시간보다 늦을 수 없습니다.");
        }
        
        if (requestDto.getStartTime().isBefore(LocalDateTime.now().minusHours(1))) {
            throw new IllegalArgumentException("과거 시간으로 일정을 생성할 수 없습니다.");
        }
    }
}

