package com.top.talent.management.exception;

public class CorruptedFileException extends RuntimeException {
    public CorruptedFileException(String message) {
        super(message);
    }
}
