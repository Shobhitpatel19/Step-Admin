package com.top.talent.management.exception;

import lombok.Getter;

import java.util.Set;

@Getter
public class EngXExtraMileRatingException extends RuntimeException {
    private final Set<String> errorMessages;

    public EngXExtraMileRatingException(Set<String> messages) {
        super(String.join(", ", messages));
        this.errorMessages = messages;
    }
    public EngXExtraMileRatingException(String message) {
        super(message);
        this.errorMessages = Set.of(message);
    }
}
