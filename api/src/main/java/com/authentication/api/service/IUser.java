package com.authentication.api.service;

import com.authentication.api.entity.User;
import com.authentication.api.exception.EmailAlreadyExistException;

public interface IUser {
    User addUser(User user) throws EmailAlreadyExistException;
}
