package org.example.wishlist2.exception;

public class DatabaseExceptionHandler extends RuntimeException {
    public DatabaseExceptionHandler(String message, Throwable cause) {
        super(message, cause);
    }
}