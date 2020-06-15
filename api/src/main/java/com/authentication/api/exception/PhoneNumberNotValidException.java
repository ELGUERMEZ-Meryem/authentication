package com.authentication.api.exception;

/**
 *  phone number not valid exception
 */
public class PhoneNumberNotValidException extends Exception {
    public PhoneNumberNotValidException(String message) {
        super(message);
    }
}
