package me.dineka.books_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidBookGenreException extends RuntimeException {
    public InvalidBookGenreException(String message) {
        super(message);
    }
}
