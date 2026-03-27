package com.top.talent.management.exception;

import lombok.Getter;

import java.util.Set;

@Getter
public class CultureScoreException extends RuntimeException {
  private final Set<String> errorMessages;

  public CultureScoreException(Set<String> messages) {
    super(String.join(", ", messages));
    this.errorMessages = messages;
  }

  public CultureScoreException(String message) {
    super(message);
    this.errorMessages = Set.of(message);
  }
}