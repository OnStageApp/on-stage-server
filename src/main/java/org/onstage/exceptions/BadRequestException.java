package org.onstage.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseException {
    BadRequestException(String errorDescription) {
        super(HttpStatus.BAD_REQUEST, 0, "BAD_REQUEST", errorDescription, null);
    }
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

    public static BadRequestException accessDenied() {
        return new BadRequestException(8, "ACCESS_DENIED", "User is not a leader to perform this action");
    }

    public static BadRequestException stagerAlreadyCreated() {
        return new BadRequestException(9, "STAGER_ALREADY_CREATED", "Stager already created for this event and user");
    }

    public static BadRequestException userAlreadyInTeam() {
        return new BadRequestException(10, "USER_ALREADY_IN_TEAM", "User is already in the team");
    }

    public static BadRequestException emailNotSent() {
        return new BadRequestException(11, "EMAIL_NOT_SENT", "Email was not sent");
    }

    public static BadRequestException plansNotMatching() {
        return new BadRequestException(12, "PLAN_NOT_MATCHING", "Current plan does not match the requested action");
    }

    public static BadRequestException teamMemberAlreadyExists() {
        return new BadRequestException(13, "TEAM_MEMBER_ALREADY_EXISTS", "Team member already exists");
    }

    public static BadRequestException transferFailed() {
        return new BadRequestException(14, "SUBSCRIPTION_TRANSFER_FAILED", "Subscription transfer failed");
    }

    public static BadRequestException duplicateUsername(String username) {
        return new BadRequestException(15, "DUPLICATE_USERNAME", "Username '" + username + "' already exists");
    }

    public static BadRequestException duplicateEmail(String email) {
        return new BadRequestException(15, "DUPLICATE_USERNAME", "Email '" + email + "' already exists");
    }
}
