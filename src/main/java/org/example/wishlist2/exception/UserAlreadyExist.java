package org.example.wishlist2.exception;

public class UserAlreadyExist extends RuntimeException {
    public UserAlreadyExist(String message) {
        super(message);
    }

    //use for both name/email unique datasat vi bruger

}