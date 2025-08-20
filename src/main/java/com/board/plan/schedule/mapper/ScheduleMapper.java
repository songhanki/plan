package com.board.plan.schedule.mapper;

import com.board.plan.schedule.model.Schedule;
import com.board.plan.schedule.model.ScheduleParticipant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 일정 관리 Mapper
 */
@Mapper
public interface ScheduleMapper {
    
    // 일정 생성
    int insertSchedule(Schedule schedule);
    
    // 일정 수정
    int updateSchedule(Schedule schedule);
    
    // 일정 삭제
    int deleteSchedule(@Param("scheduleId") String scheduleId);
    
    // 일정 조회 (ID로)
    Schedule selectScheduleById(@Param("scheduleId") String scheduleId);
    
    // 내 일정 목록 조회
    List<Schedule> selectMySchedules(@Param("memberId") String memberId, 
                                   @Param("startDate") LocalDate startDate, 
                                   @Param("endDate") LocalDate endDate);
    
    // 모든 일정 목록 조회 (관리자/팀장용)
    List<Schedule> selectAllSchedules(@Param("startDate") LocalDate startDate, 
                                    @Param("endDate") LocalDate endDate);
    
    // 팀/전사 일정 목록 조회 
    List<Schedule> selectTeamSchedules(@Param("startDate") LocalDate startDate, 
                                     @Param("endDate") LocalDate endDate);
    
    // 일정 참여자 추가
    int insertScheduleParticipant(ScheduleParticipant participant);
    
    // 일정 참여자 삭제
    int deleteScheduleParticipants(@Param("scheduleId") String scheduleId);
    
    // 일정 참여자 목록 조회
    List<ScheduleParticipant> selectScheduleParticipants(@Param("scheduleId") String scheduleId);
    
    // 일정 참여자 상태 업데이트
    int updateParticipationStatus(@Param("scheduleId") String scheduleId, 
                                @Param("memberId") String memberId, 
                                @Param("status") String status);
    
    // 일정 소유자 확인
    boolean isScheduleOwner(@Param("scheduleId") String scheduleId, @Param("memberId") String memberId);
}

