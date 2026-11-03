package org.example.wishlist2.exception;

public class UserValidation extends RuntimeException {
    public UserValidation(String message) {
        super(message);
    }
}
