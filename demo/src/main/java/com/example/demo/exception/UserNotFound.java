package com.example.demo;

public class UserNotFound extends RuntimeException {
    public UserNotFound(int id) {
        super("User with id: " + id + "was not found");
    }
}