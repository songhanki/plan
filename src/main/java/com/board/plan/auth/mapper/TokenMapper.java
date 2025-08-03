package com.board.plan.auth.mapper;

import com.board.plan.auth.model.Token;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TokenMapper {
    void saveToken(Token token);
    Token findByMemberId(String memberId);
    void updateToken(Token token);
    void deleteByMemberId(String memberId);
    Token findByAccessToken(String accessToken);
}