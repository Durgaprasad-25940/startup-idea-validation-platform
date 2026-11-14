package com.startup.validator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * UsernameAlreadyExistsException is thrown when registration is attempted with a username that is already in use.
 * 
 * `@ResponseStatus(value = HttpStatus.BAD_REQUEST)` maps this exception automatically to HTTP status 400.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UsernameAlreadyExistsException extends RuntimeException {
    
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
