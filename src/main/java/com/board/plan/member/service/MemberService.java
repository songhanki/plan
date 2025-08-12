package com.board.plan.member.service;

import com.board.plan.member.dto.MemberDto;
import com.board.plan.core.exception.DuplicateEntryException;
import com.board.plan.core.exception.UserNotFoundException;
import com.board.plan.core.util.PasswordUtil;
import com.board.plan.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberMapper memberMapper;

    /**
     * 다음 member_id 자동 생성 (user-001, user-002... 형식)
     */
    @Transactional(readOnly = true) // 동시성 문제 해결을 위해 트랜잭션 사용
    private String generateNextMemberId() {
        Integer maxNumber = memberMapper.findMaxMemberIdNumber();
        int nextNumber = (maxNumber != null) ? maxNumber + 1 : 1;
        return String.format("user-%03d", nextNumber);
    }

    /**
     * 사용자 생성
     */
    public MemberDto createMember(MemberDto memberDto) {
        log.info("Creating member with email: {}", memberDto.getEmail());
        
        // 이메일 중복 확인
        if (memberMapper.existsByEmail(memberDto.getEmail())) {
            String errorMsg = String.format("이메일이 이미 존재합니다: %s", memberDto.getEmail());
            log.warn("Member creation failed - {}", errorMsg);
            throw new DuplicateEntryException("이메일", memberDto.getEmail());
        }
        
        // 전화번호 중복 확인
        if (memberMapper.existsByPhoneNumber(memberDto.getPhoneNumber())) {
            String errorMsg = String.format("전화번호가 이미 존재합니다: %s", memberDto.getPhoneNumber());
            log.warn("Member creation failed - {}", errorMsg);
            throw new DuplicateEntryException("전화번호", memberDto.getPhoneNumber());
        }

        // member_id 자동 생성
        String nextMemberId = generateNextMemberId();
        memberDto.setMemberId(nextMemberId);
        
        // 기본 역할 설정 (일반 사용자)
        if (memberDto.getRoleId() == null || memberDto.getRoleId().isEmpty()) {
            memberDto.setRoleId("role-user");
        }
        
        // 기본 상태 설정
        if (memberDto.getStatus() == null || memberDto.getStatus().isEmpty()) {
            memberDto.setStatus("ACTIVE");
        }

        // 비밀번호 SHA256 암호화
        String encryptedPassword = PasswordUtil.encryptPassword(memberDto.getPassword());
        memberDto.setPassword(encryptedPassword);
        
        // 회원 등록
        memberMapper.insertMember(memberDto);
        
        log.info("Member created successfully with ID: {}", memberDto.getMemberId());
        
        // 비밀번호 제외하고 반환
        memberDto.setPassword(null);
        return memberDto;
    }

    /**
     * 모든 사용자 조회
     */
    @Transactional(readOnly = true)
    public List<MemberDto> getAllMembers() {
        log.info("Fetching all members");
        List<MemberDto> members = memberMapper.findAllMembers();
        return members;
    }

    /**
     * ID로 사용자 조회
     */
    @Transactional(readOnly = true)
    public MemberDto getMemberById(Long id) {
        log.info("Fetching member with ID: {}", id);
        MemberDto member = memberMapper.findMemberById(id);
        if (member == null) {
            String errorMsg = String.format("조회할 사용자를 찾을 수 없습니다. ID: %d", id);
            log.warn("Member lookup failed - {}", errorMsg);
            throw new UserNotFoundException(id);
        }
        return member;
    }

    /**
     * member_id로 사용자 조회
     */
    @Transactional(readOnly = true)
    public MemberDto getMemberByMemberId(String memberId) {
        log.info("Fetching member with member_id: {}", memberId);
        MemberDto member = memberMapper.findMemberByMemberId(memberId);
        if (member == null) {
            String errorMsg = String.format("조회할 사용자를 찾을 수 없습니다. member_id: %s", memberId);
            log.warn("Member lookup failed - {}", errorMsg);
            throw new UserNotFoundException("사용자 ID: " + memberId + "를 찾을 수 없습니다.");
        }
        return member;
    }

    /**
     * 사용자 정보 수정
     */
    public MemberDto updateMember(Long id, MemberDto memberDto) {
        log.info("Updating member with ID: {}", id);
        
        // 사용자 존재 확인
        MemberDto existingMember = memberMapper.findMemberById(id);
        if (existingMember == null) {
            String errorMsg = String.format("업데이트할 사용자를 찾을 수 없습니다. ID: %d", id);
            log.warn("Member update failed - {}", errorMsg);
            throw new UserNotFoundException(id);
        }

        // 이메일 중복 확인 (자신 제외)
        MemberDto memberWithEmail = memberMapper.findMemberByEmail(memberDto.getEmail());
        if (memberWithEmail != null && !memberWithEmail.getMemberId().equals(existingMember.getMemberId())) {
            String errorMsg = String.format("이메일이 다른 사용자에 의해 사용중입니다: %s (충돌 member_id: %s)", 
                memberDto.getEmail(), memberWithEmail.getMemberId());
            log.warn("Member update failed - {}", errorMsg);
            throw new DuplicateEntryException("이메일", memberDto.getEmail());
        }

        // 전화번호 중복 확인 (자신 제외)
        MemberDto memberWithPhoneNumber = memberMapper.findMemberByPhoneNumber(memberDto.getPhoneNumber());
        if (memberWithPhoneNumber != null && !memberWithPhoneNumber.getMemberId().equals(existingMember.getMemberId())) {
            String errorMsg = String.format("전화번호가 다른 사용자에 의해 사용중입니다: %s (충돌 member_id: %s)", 
                memberDto.getPhoneNumber(), memberWithPhoneNumber.getMemberId());
            log.warn("Member update failed - {}", errorMsg);
            throw new DuplicateEntryException("전화번호", memberDto.getPhoneNumber());
        }

        // member_id는 기존 값 유지
        memberDto.setMemberId(existingMember.getMemberId());

        // 비밀번호 SHA256 암호화
        String encryptedPassword = PasswordUtil.encryptPassword(memberDto.getPassword());
        memberDto.setPassword(encryptedPassword);

        memberMapper.updateMember(memberDto);
        
        // 업데이트된 사용자 정보 조회
        MemberDto updatedMember = memberMapper.findMemberByMemberId(memberDto.getMemberId());
        log.info("Member updated successfully with member_id: {}", memberDto.getMemberId());
        
        // 비밀번호 제외하고 반환
        updatedMember.setPassword(null);
        return updatedMember;
    }

    /**
     * 사용자 삭제
     */
    public void deleteMember(Long id) {
        log.info("Deleting member with ID: {}", id);
        
        // 사용자 존재 확인
        MemberDto member = memberMapper.findMemberById(id);
        if (member == null) {
            String errorMsg = String.format("사용자를 찾을 수 없습니다. ID: %d", id);
            log.warn("Member lookup failed - {}", errorMsg);
            throw new UserNotFoundException(id);
        }

        memberMapper.deleteMember(id);
        log.info("Member deleted successfully with ID: {}", id);
    }

    /**
     * member_id로 사용자 삭제
     */
    public void deleteMemberByMemberId(String memberId) {
        log.info("Deleting member with member_id: {}", memberId);
        
        // 사용자 존재 확인
        MemberDto member = memberMapper.findMemberByMemberId(memberId);
        if (member == null) {
            String errorMsg = String.format("사용자를 찾을 수 없습니다. member_id: %s", memberId);
            log.warn("Member lookup failed - {}", errorMsg);
            throw new UserNotFoundException("사용자 ID: " + memberId + "를 찾을 수 없습니다.");
        }

        memberMapper.deleteMemberByMemberId(memberId);
        log.info("Member deleted successfully with member_id: {}", memberId);
    }
} 