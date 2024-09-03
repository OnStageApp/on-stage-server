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
        return new BadRequestException(7, "STAGER_NOT_FOUND", "Stager was not found");
    }

    public static BadRequestException songNotFound() {
        return new BadRequestException(8, "SONG_NOT_FOUND", "Song was not found");
    }

    public static BadRequestException eventNotFound() {
        return new BadRequestException(9, "EVENT_NOT_FOUND", "Event was not found");
    }

    public static BadRequestException teamNotFound() {
        return new BadRequestException(10, "TEAM_NOT_FOUND", "Team was not found");
    }

    public static BadRequestException artistNotFound() {
        return new BadRequestException(11, "ARTIST_NOT_FOUND", "Artist was not found");
    }

    public static BadRequestException teamMemberNotFound() {
        return new BadRequestException(12, "TEAM_MEMBER_NOT_FOUND", "Team member was not found");
    }

    public static BadRequestException rehearsalNotFound() {
        return new BadRequestException(13, "REHEARSAL_NOT_FOUND", "Rehearsal was not found");
    }

    public static BadRequestException userNotFound() {
        return new BadRequestException(14, "USER_NOT_FOUND", "User was not found");
    }
}
