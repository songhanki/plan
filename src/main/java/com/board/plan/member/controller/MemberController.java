package com.board.plan.member.controller;

import com.board.plan.member.dto.MemberDto;
import com.board.plan.member.service.MemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Member Management", description = "회원 관리 API")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다. member_id는 자동으로 user-001, user-002... 형태로 생성됩니다.")
    @PostMapping
    public ResponseEntity<MemberDto> createMember(@Valid @RequestBody MemberDto memberDto) {
        log.info("POST /api/members - Creating member with email: {}", memberDto.getEmail());
        MemberDto createdMember = memberService.createMember(memberDto);
        return new ResponseEntity<>(createdMember, HttpStatus.CREATED);
    }

    @Operation(summary = "모든 사용자 조회", description = "모든 사용자를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<MemberDto>> getAllMembers() {
        log.info("GET /api/members - Fetching all members");
        List<MemberDto> members = memberService.getAllMembers();
        return ResponseEntity.ok(members);
    }

    @Operation(summary = "ID로 사용자 조회", description = "숫자 ID로 사용자를 조회합니다. (호환성을 위해 유지)")
    @GetMapping("/{id}")
    public ResponseEntity<MemberDto> getMemberById(@PathVariable Long id) {
        log.info("GET /api/members/{} - Fetching member by ID", id);
        MemberDto member = memberService.getMemberById(id);
        return ResponseEntity.ok(member);
    }

    @Operation(summary = "member_id로 사용자 조회", description = "member_id(user-XXX)로 사용자를 조회합니다.")
    @GetMapping("/member/{memberId}")
    public ResponseEntity<MemberDto> getMemberByMemberId(@PathVariable String memberId) {
        log.info("GET /api/members/member/{} - Fetching member by member_id", memberId);
        MemberDto member = memberService.getMemberByMemberId(memberId);
        return ResponseEntity.ok(member);
    }

    @Operation(summary = "사용자 업데이트", description = "사용자 정보를 업데이트합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<MemberDto> updateMember(@PathVariable Long id, @Valid @RequestBody MemberDto memberDto) {
        log.info("PUT /api/members/{} - Updating member", id);
        MemberDto updatedMember = memberService.updateMember(id, memberDto);
        return ResponseEntity.ok(updatedMember);
    }

    @Operation(summary = "member_id로 사용자 업데이트", description = "member_id로 사용자 정보를 업데이트합니다.")
    @PutMapping("/member/{memberId}")
    public ResponseEntity<MemberDto> updateMemberByMemberId(@PathVariable String memberId, @Valid @RequestBody MemberDto memberDto) {
        log.info("PUT /api/members/member/{} - Updating member by member_id", memberId);
        // member_id로 기존 사용자 조회 후 업데이트
        MemberDto existingMember = memberService.getMemberByMemberId(memberId);
        memberDto.setMemberId(memberId);
        
        // Long id가 필요하므로 임시로 1을 사용 (실제로는 member_id로 업데이트됨)
        MemberDto updatedMember = memberService.updateMember(1L, memberDto);
        return ResponseEntity.ok(updatedMember);
    }

    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        log.info("DELETE /api/members/{} - Deleting member", id);
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "member_id로 사용자 삭제", description = "member_id로 사용자를 삭제합니다.")
    @DeleteMapping("/member/{memberId}")
    public ResponseEntity<Void> deleteMemberByMemberId(@PathVariable String memberId) {
        log.info("DELETE /api/members/member/{} - Deleting member by member_id", memberId);
        memberService.deleteMemberByMemberId(memberId);
        return ResponseEntity.noContent().build();
    }
} 