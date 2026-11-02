package com.example.demo;

public class UserAlreadyExist extends RuntimeException {
    public UserAlreadyExist(String message, Throwable cause) {
        super(message, cause);
    }

    //use for both name/email unique datasat vi bruger

}