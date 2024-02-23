package org.onstage.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class BaseException extends RuntimeException{
    private final HttpStatus status;
    private final int errorCode;
    private final String errorName;
    private final String errorDescription;
}
