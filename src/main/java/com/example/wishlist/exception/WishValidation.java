package com.example.wishlist.exception;

/** Ugyldigt input til seddel eller ønske. */
public class WishValidation extends RuntimeException {
    public WishValidation(String message) {
        super(message);
    }
}
