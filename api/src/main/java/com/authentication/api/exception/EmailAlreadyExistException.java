package com.authentication.api.exception;

/**
 * Email already exist exception
 */
public class EmailAlreadyExistException extends Exception {
    public EmailAlreadyExistException(String message) {
        super(message);
    }
}
