package com.example.demo;

public class UserAlreadyExist extends RuntimeException {
    public UserAlreadyExist(String message, Throwable cause) {
        super(message, cause);
    }

    //use for both email/whatever unique datasat vi bruger

}