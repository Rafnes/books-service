package me.dineka.books_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AuthorAlreadyExistsException extends RuntimeException {
    public AuthorAlreadyExistsException(String message) {
        super(message);
    }
}
