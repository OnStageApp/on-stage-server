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

    public static BadRequestException loginError(String message) {
        return new BadRequestException(4, "LOGIN_ERROR", message);
    }

    public static BadRequestException firebaseTokenMissing() {
        return new BadRequestException(5, "LOGIN_MISSING_TOKEN", "No token was found");
    }

    public static BadRequestException stagerAlreadyCreated() {
        return new BadRequestException(6, "STAGER_ALREADY_CREATED", "Stager already created for this event and user");
    }

    public static BadRequestException stagerNotFound() {
        return new BadRequestException(7, "STAGER_NOT_FOUND", "Stager not found");
    }

    public static BadRequestException songNotFound() {
        return new BadRequestException(7, "SONG_NOT_FOUND", "Song not found");
    }

    public static BadRequestException eventNotFound() {
        return new BadRequestException(7, "EVENT_NOT_FOUND", "Event not found");
    }
}
