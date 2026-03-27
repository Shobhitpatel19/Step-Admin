package com.top.talent.management.exception;

import lombok.Getter;

import java.util.Set;

@Getter
public class InvalidCandidateException extends RuntimeException {
    private final Set<String> errorMessages;

    public InvalidCandidateException(Set<String> messages) {
        super(String.join(", ", messages));
        this.errorMessages = messages;
    }
    public InvalidCandidateException(String message) {
        super(message);
        this.errorMessages = Set.of(message);
    }
}
