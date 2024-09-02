package org.onstage.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@JsonIgnoreProperties(value = {"status", "message", "localizedMessage", "cause", "stackTrace", "suppressed"})
public class BaseException extends RuntimeException {
    private final HttpStatus status;
    private final int errorCode;
    private final String errorName;
    private final String errorDescription;
}
