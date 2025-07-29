package com.board.plan.member.mapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.board.plan.member.dto.MemberDto;

import java.util.List;

@Mapper
public interface MemberMapper {
    
    void insertMember(MemberDto member);
    
    List<MemberDto> findAllMembers();
    
    MemberDto findMemberById(@Param("id") Long id);
    
    MemberDto findMemberByMemberId(@Param("memberId") String memberId);
    
    MemberDto findMemberByEmail(@Param("email") String email);
    
    MemberDto findMemberByPhoneNumber(@Param("phoneNumber") String phoneNumber);
    
    void updateMember(MemberDto member);
    
    void deleteMember(@Param("id") Long id);
    
    void deleteMemberByMemberId(@Param("memberId") String memberId);
    
    boolean existsByEmail(@Param("email") String email);
    
    boolean existsByPhoneNumber(@Param("phoneNumber") String phoneNumber);
    
    /**
     * 최대 member_id의 번호 부분을 조회 (user-XXX에서 XXX 부분)
     * @return 최대 번호 (예: user-005에서 5 반환)
     */
    Integer findMaxMemberIdNumber();
} 