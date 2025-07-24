package com.board.plan.controller;

import com.board.plan.dto.UserDto;
import com.board.plan.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "회원 관리 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원 생성", description = "새로운 회원을 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "회원 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "409", description = "이메일 또는 전화번호 중복")
    })
    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @Parameter(description = "생성할 회원 정보", required = true)
            @Valid @RequestBody UserDto userDto) {
        
        log.info("POST /api/users - Creating user with email: {}", userDto.getEmail());
        UserDto createdUser = userService.createUser(userDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @Operation(summary = "모든 회원 조회", description = "등록된 모든 회원을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "회원 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("GET /api/users - Fetching all users");
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "특정 회원 조회", description = "ID로 특정 회원을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "회원 조회 성공"),
        @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(
            @Parameter(description = "회원 ID", required = true)
            @PathVariable Long id) {
        
        log.info("GET /api/users/{} - Fetching user by ID", id);
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "회원 정보 수정", description = "특정 회원의 정보를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "이메일 또는 전화번호 중복")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "회원 ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "수정할 회원 정보", required = true)
            @Valid @RequestBody UserDto userDto) {
        
        log.info("PUT /api/users/{} - Updating user", id);
        UserDto updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "회원 삭제", description = "특정 회원을 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "회원 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "회원 ID", required = true)
            @PathVariable Long id) {
        
        log.info("DELETE /api/users/{} - Deleting user", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
} 