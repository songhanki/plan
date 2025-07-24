package com.board.plan.mapper;

import com.board.plan.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    
    void insertUser(User user);
    
    List<User> findAllUsers();
    
    User findUserById(@Param("id") Long id);
    
    User findUserByEmail(@Param("email") String email);
    
    User findUserByPhoneNumber(@Param("phoneNumber") String phoneNumber);
    
    void updateUser(User user);
    
    void deleteUser(@Param("id") Long id);
    
    boolean existsByEmail(@Param("email") String email);
    
    boolean existsByPhoneNumber(@Param("phoneNumber") String phoneNumber);
} 