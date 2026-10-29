package com.example.demo;

public class DatabaseExceptionHandler extends RuntimeException {
    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}