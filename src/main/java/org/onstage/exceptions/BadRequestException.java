package org.onstage.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseException {
    private BadRequestException(int errorCode, String errorName, String errorDescription) {
        super(HttpStatus.BAD_REQUEST, errorCode, errorName, errorDescription, null);
    }

    private BadRequestException(int errorCode, String errorName, String errorDescription, String param) {
        super(HttpStatus.BAD_REQUEST, errorCode, errorName, errorDescription, param);
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

    public static BadRequestException resourceNotFound(String param) {
        return new BadRequestException(6, "RESOURCE_NOT_FOUND", "Resource not found", param);
    }

    public static BadRequestException permissionDenied(String param) {
        return new BadRequestException(7, "PERMISSION_DENIED", "Team has no permission to use this functionality", param);
    }

    public static BadRequestException stagerAlreadyCreated() {
        return new BadRequestException(6, "STAGER_ALREADY_CREATED", "Stager already created for this event and user");
    }

    public static BadRequestException userAlreadyInTeam() {
        return new BadRequestException(16, "USER_ALREADY_IN_TEAM", "User is already in the team");
    }

    public static BadRequestException emailNotSent() {
        return new BadRequestException(16, "EMAIL_NOT_SENT", "Email was not sent");
    }
}
