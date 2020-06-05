package com.authentication.api.service;

import com.authentication.api.entity.User;
import com.authentication.api.exception.EmailAlreadyExistException;

/**
 * User interface
 */

public interface IUser {
    /**
     * Add user if he is not already exist in our database
     *
     * @param user informations
     * @return registered user
     * @throws EmailAlreadyExistException if he already has an account
     */
    User addUser(User user) throws EmailAlreadyExistException;

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
