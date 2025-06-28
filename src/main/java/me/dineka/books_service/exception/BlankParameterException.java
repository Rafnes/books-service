package me.dineka.books_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BlankParameterException extends RuntimeException {
    public BlankParameterException(String message) {
        super(message);
    }
}
