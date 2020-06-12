package com.authentication.api.controller;

import com.authentication.api.entity.User;
import com.authentication.api.exception.EmailAlreadyExistException;
import com.authentication.api.exception.PhoneNumberAlreadyExistException;
import com.authentication.api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/public")
public class HelloController {
    private final UserService userService;

    public HelloController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Call addUser service to sign up the user if he is not registered in the database
     * Or throw exception EmailAlreadyExistException if he already exist in the database
     *
     * @param user get user informations from front end to sign up the user if he is not in the database
     * @return user
     */
    @PostMapping("/signUp")
    public ResponseEntity<?> singUp(@RequestBody User user) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(user));
        } catch (EmailAlreadyExistException | PhoneNumberAlreadyExistException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Call verifySecretCode service to verify the secret code
     *
     * @param user get the secret code and user email
     */
    @PostMapping("/verificationCode")
    public ResponseEntity<?> verification_code(@RequestBody User user) {
        if (userService.verifySecretCode(user.getEmail(), user.getCode_2fa())) {
            return ResponseEntity.status(HttpStatus.OK).body(true);
        }
        //if the verification code is not correct we send a bad request
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("verification code that you enter is not correct, please try again");
    }
}
