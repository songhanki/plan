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
    
    MemberDto findMemberByEmail(@Param("email") String email);
    
    MemberDto findMemberByPhoneNumber(@Param("phoneNumber") String phoneNumber);
    
    void updateMember(MemberDto member);
    
    void deleteMember(@Param("id") Long id);
    
    boolean existsByEmail(@Param("email") String email);
    
    boolean existsByPhoneNumber(@Param("phoneNumber") String phoneNumber);
} 