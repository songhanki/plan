package com.board.plan.service;

import com.board.plan.dto.UserDto;
import com.board.plan.exception.DuplicateEntryException;
import com.board.plan.exception.UserNotFoundException;
import com.board.plan.mapper.UserMapper;
import com.board.plan.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자 생성
     */
    public UserDto createUser(UserDto userDto) {
        log.info("Creating user with email: {}", userDto.getEmail());
        
        // 이메일 중복 확인
        if (userMapper.existsByEmail(userDto.getEmail())) {
            throw new DuplicateEntryException("이메일", userDto.getEmail());
        }
        
        // 전화번호 중복 확인
        if (userMapper.existsByPhoneNumber(userDto.getPhoneNumber())) {
            throw new DuplicateEntryException("전화번호", userDto.getPhoneNumber());
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        
        User user = User.builder()
                .email(userDto.getEmail())
                .password(encodedPassword)
                .name(userDto.getName())
                .phoneNumber(userDto.getPhoneNumber())
                .build();

        userMapper.insertUser(user);
        
        log.info("User created successfully with ID: {}", user.getId());
        return convertToDto(user);
    }

    /**
     * 모든 사용자 조회
     */
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        log.info("Fetching all users");
        List<User> users = userMapper.findAllUsers();
        return users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * ID로 사용자 조회
     */
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        log.info("Fetching user with ID: {}", id);
        User user = userMapper.findUserById(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }
        return convertToDto(user);
    }

    /**
     * 사용자 정보 수정
     */
    public UserDto updateUser(Long id, UserDto userDto) {
        log.info("Updating user with ID: {}", id);
        
        // 사용자 존재 확인
        User existingUser = userMapper.findUserById(id);
        if (existingUser == null) {
            throw new UserNotFoundException(id);
        }

        // 이메일 중복 확인 (자신 제외)
        User userWithEmail = userMapper.findUserByEmail(userDto.getEmail());
        if (userWithEmail != null && !userWithEmail.getId().equals(id)) {
            throw new DuplicateEntryException("이메일", userDto.getEmail());
        }

        // 전화번호 중복 확인 (자신 제외)
        User userWithPhoneNumber = userMapper.findUserByPhoneNumber(userDto.getPhoneNumber());
        if (userWithPhoneNumber != null && !userWithPhoneNumber.getId().equals(id)) {
            throw new DuplicateEntryException("전화번호", userDto.getPhoneNumber());
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        
        User user = User.builder()
                .id(id)
                .email(userDto.getEmail())
                .password(encodedPassword)
                .name(userDto.getName())
                .phoneNumber(userDto.getPhoneNumber())
                .build();

        userMapper.updateUser(user);
        
        // 업데이트된 사용자 정보 조회
        User updatedUser = userMapper.findUserById(id);
        log.info("User updated successfully with ID: {}", id);
        return convertToDto(updatedUser);
    }

    /**
     * 사용자 삭제
     */
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        
        // 사용자 존재 확인
        User user = userMapper.findUserById(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }

        userMapper.deleteUser(id);
        log.info("User deleted successfully with ID: {}", id);
    }

    /**
     * User 엔티티를 UserDto로 변환 (비밀번호 제외)
     */
    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
} 