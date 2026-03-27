package com.top.talent.management.exception;

import java.util.Map;

public record ErrorResponse(String errorMessage, Map<String, String> errors) {}