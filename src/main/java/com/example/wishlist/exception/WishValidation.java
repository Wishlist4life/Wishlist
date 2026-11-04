package com.example.wishlist.exception;

/** Ugyldigt input til en oenskeseddel eller et oenske (tomt navn, ugyldig pris/link osv.). */
public class WishValidation extends RuntimeException {
    public WishValidation(String message) {
        super(message);
    }
}
