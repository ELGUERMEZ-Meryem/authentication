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

    User verifySecretCode(String username, String code);
}
