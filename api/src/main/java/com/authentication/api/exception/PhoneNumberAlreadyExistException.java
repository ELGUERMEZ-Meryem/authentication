package com.authentication.api.exception;

/**
 * Phone number already exist exception
 */
public class PhoneNumberAlreadyExistException extends Exception{
    public PhoneNumberAlreadyExistException(String message) {
        super(message);
    }
}
