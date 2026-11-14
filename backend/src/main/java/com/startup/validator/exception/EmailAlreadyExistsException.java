package com.startup.validator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * EmailAlreadyExistsException is thrown when a user registration is attempted with an email already in use.
 * 
 * `@ResponseStatus(value = HttpStatus.BAD_REQUEST)` maps this exception automatically to HTTP status 400.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EmailAlreadyExistsException extends RuntimeException {
    
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
