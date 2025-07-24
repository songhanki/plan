package com.board.plan.exception;

public class DuplicateEntryException extends RuntimeException {
    public DuplicateEntryException(String message) {
        super(message);
    }
    
    public DuplicateEntryException(String field, String value) {
        super(String.format("이미 존재하는 %s입니다: %s", field, value));
    }
} 