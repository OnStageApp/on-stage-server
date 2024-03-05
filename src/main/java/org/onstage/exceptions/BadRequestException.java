package org.onstage.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseException {
    private BadRequestException(int errorCode, String errorName, String errorDescription) {
        super(HttpStatus.BAD_REQUEST, errorCode, errorName, errorDescription);
    }

    public static BadRequestException unknownError() {
        return new BadRequestException(1, "UNKNOWN_ERROR", "Unknown error");
    }

    public static BadRequestException missingField() {
        return new BadRequestException(2, "MISSING_FIELD", "The request is invalid, some fields are missing");
    }

    public static BadRequestException invalidRequest() {
        return new BadRequestException(3, "INVALID_REQUEST", "The request could not be parsed as a valid JSON");
    }
}
