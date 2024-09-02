package org.onstage.common.exceptions;

import org.onstage.exceptions.BadRequestException;
import org.onstage.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleHttpException(BadRequestException ex, WebRequest request) {
        return new ResponseEntity<>(ex, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex, WebRequest request) {
        return new ResponseEntity<>(new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, 0, "InternalServerError", "An unexpected error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


