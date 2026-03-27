package com.top.talent.management.exception;

import com.alibaba.excel.exception.ExcelAnalysisException;
import com.top.talent.management.constants.ErrorMessages;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Inside handleIllegalArgumentException {}", e.getMessage());
        return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
        log.error("Inside handleUserNotFoundException {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSignature(SignatureException e) {
        log.error("Inside handleInvalidSignature {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(buildErrorResponse(ErrorMessages.INVALID_JWT_SIGN));
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJwt(MalformedJwtException e) {
        log.error("Inside handleMalformedJwt {}", e.getMessage());
        return ResponseEntity.badRequest().body(buildErrorResponse(ErrorMessages.INVALID_JWT_TOKEN));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwt(ExpiredJwtException e) {
        log.error("Inside handleExpiredJwt {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(buildErrorResponse(ErrorMessages.EXPIRED_JWT));
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedJwt(UnsupportedJwtException e) {
        log.error("Inside handleUnsupportedJwt {}", e.getMessage());
        return ResponseEntity.badRequest().body(buildErrorResponse(ErrorMessages.UNSUPPORTED_JWT));
    }

    @ExceptionHandler(InvalidFileFormatException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFileFormatException(InvalidFileFormatException e) {
        log.error("Invalid File Format {}", e.getMessage());
        return ResponseEntity.badRequest().body(buildErrorResponse(ErrorMessages.INVALID_FILE_FORMAT));
    }

    // Handle EmptyFileException
    @ExceptionHandler(EmptyFileException.class)
    public ResponseEntity<ErrorResponse> handleEmptyFileException(EmptyFileException e) {
        log.error("Empty File {}", e.getMessage());
        return ResponseEntity.badRequest().body(buildErrorResponse(ErrorMessages.EMPTY_FILE));
    }

    @ExceptionHandler(IOException.class)
    public void handleIOException(IOException ex) {
        throw new CorruptedFileException(ErrorMessages.CORRUPTED_EXCEL_FILE);
    }

    @ExceptionHandler(ExcelAnalysisException.class)
    public void handleExcelAnalysisException(IOException ex) {
        throw new CorruptedFileException(ErrorMessages.CORRUPTED_EXCEL_FILE);
    }

    @ExceptionHandler(CorruptedFileException.class)
    public ResponseEntity<ErrorResponse> handleCorruptedFileException(CorruptedFileException e) {
        log.error("Corrupted File {}", e.getMessage());
        return ResponseEntity.badRequest().body(buildErrorResponse(ErrorMessages.CORRUPTED_EXCEL_FILE));
    }

    // Handle all other exceptions not specifically handled
    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception e) {
        log.error("An unexpected error occurred {}", e.getStackTrace());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("unauthorized access {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildErrorResponse(ErrorMessages.ACCESS_DENIED));
    }

    @ExceptionHandler(InvalidCandidateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCandidate(InvalidCandidateException e) {
        log.error("Candidate is Invalid: {}", e.getMessage());

        Map<String, String> errors = new HashMap<>();
        int i = 1;

        for (String errorMessage : e.getErrorMessages()) {
            errors.put(ErrorMessages.MESSAGE + i++, errorMessage);
        }

        ErrorResponse errorResponse = new ErrorResponse(ErrorMessages.VALIDATION_ERROR, errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(PracticeDelegationException.class)
    public ResponseEntity<ErrorResponse> handlePracticeDelegationException(PracticeDelegationException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.badRequest().body(buildErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(EngXExtraMileRatingException.class)
    public ResponseEntity<ErrorResponse> handleEngXExtraMileRatingException(EngXExtraMileRatingException ex) {
        log.error(ErrorMessages.ERROR_PROCESSING , ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        int i = 1;

        for (String errorMessage : ex.getErrorMessages()) {
            errors.put(ErrorMessages.MESSAGE + i++, errorMessage);
        }

        ErrorResponse errorResponse = new ErrorResponse(ErrorMessages.VALIDATION_ERROR, errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(CandidateAspirationException.class)
    public ResponseEntity<ErrorResponse> handleCandidateAspirationException(CandidateAspirationException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(VersionException.class)
    public ResponseEntity<ErrorResponse> handleVersionException(VersionException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(CultureScoreException.class)
    public ResponseEntity<ErrorResponse> handleCultureScoreException(CultureScoreException ex) {
        log.error(ErrorMessages.ERROR_PROCESSING, ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        int i = 1;
        for (String errorMessage : ex.getErrorMessages()) {
            errors.put(ErrorMessages.MESSAGE + i++, errorMessage);
        }

        ErrorResponse errorResponse = new ErrorResponse(ErrorMessages.VALIDATION_ERROR, errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(InvalidTalentProfileCategory.class)
    public ResponseEntity<ErrorResponse> handleInvalidTalentProfileCategory(InvalidTalentProfileCategory e){
        log.error("InvalidTalentProfileCategory: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(buildErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(TopTalentEmployeeException.class)
    public ResponseEntity<ErrorResponse> handleTopTalentEmployeeException(TopTalentEmployeeException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiEpamException(ApiException e) {
        log.error("ApiEpamException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<ErrorResponse> handleEmailException(EmailException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(PracticeRatingException.class)
    public ResponseEntity<ErrorResponse> handlePracticeRatingException(PracticeRatingException ex) {
        log.error(ex.getMessage());
        if (ErrorMessages.SELF_RATING_NOT_ALLOWED.equals(ex.getMessage())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildErrorResponse(ex.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidStatusException(InvalidStatusException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorResponse(ex.getMessage()));
    }

    private ErrorResponse buildErrorResponse(String errorMessage) {
        return new ErrorResponse(errorMessage, null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }


    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<ErrorResponse> handleNotificationsNotFoundException(NotificationException ex) {
        log.error("Notifications not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(FutureSkillException.class)
    public ResponseEntity<ErrorResponse> handleFutureSkillException(FutureSkillException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildErrorResponse(ex.getMessage()));
    }

}
