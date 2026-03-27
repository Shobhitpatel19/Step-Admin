package com.top.talent.management.exception;

import com.top.talent.management.constants.ErrorMessages;

public class NotCurrentYearException extends RuntimeException{
    public NotCurrentYearException() {
        super(ErrorMessages.NOT_CURRENT_YEAR);
    }
}
