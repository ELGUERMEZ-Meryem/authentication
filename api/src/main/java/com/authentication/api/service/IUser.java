package com.authentication.api.service;

import com.authentication.api.entity.User;
import com.authentication.api.exception.EmailAlreadyExistException;
import com.authentication.api.exception.PhoneNumberAlreadyExistException;
import com.authentication.api.exception.PhoneNumberNotValidException;

/**
 * User interface
 */

public interface IUser {
    /**
     * Add user if his email or phine number are not already exist in our database
     *
     * @param user informations
     * @return registered user
     * @throws EmailAlreadyExistException       if his email already exist
     * @throws PhoneNumberAlreadyExistException if his phone number already exist
     * @throws PhoneNumberNotValidException     if his phone number not valid
     */
    User addUser(User user) throws EmailAlreadyExistException, PhoneNumberAlreadyExistException, PhoneNumberNotValidException;

    /**
     * Read the user from the database thanks to username that we get in parameter and verifies the code
     * If the user exists and the TOTP code is valid,
     * we set the isEnabled field on the user record to 1 and responds with true
     *
     * @param username email of the user
     * @param code     secret 2fa code that we want to verify
     * @return Boolean true if the code is valid, false if not
     */
    Boolean verifySecretCode(String username, String code);
}
