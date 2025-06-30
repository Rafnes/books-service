package me.dineka.books_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidBookTitleException extends RuntimeException {
    public InvalidBookTitleException(String message) {
        super(message);
    }
}
