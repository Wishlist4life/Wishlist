package com.example.wishlist.exception;

public class UserValidation extends RuntimeException {
    public UserValidation(String message) {
        super(message);
    }
}
