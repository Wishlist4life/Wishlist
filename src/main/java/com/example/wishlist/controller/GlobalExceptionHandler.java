package com.example.wishlist.controller;

import com.example.wishlist.exception.DatabaseOperationException;
import com.example.wishlist.exception.UserNotFound;
import com.example.wishlist.exception.WishlistNotFound;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String profileNotFound(UserNotFound pde, Model model) {
        return notFound(pde.getMessage(), model);
    }

    @ExceptionHandler(WishlistNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String wishlistNotFound(WishlistNotFound ex, Model model) {
        return notFound(ex.getMessage(), model);
    }

    @ExceptionHandler(DatabaseOperationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleDatabaseOperation(DatabaseOperationException dbe, Model model) {
        return serverError(dbe.getLocalizedMessage(), model);
    }

    // Databasefejl: 500-side uden SQL-detaljer
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleDataAccess(DataAccessException ex, Model model) {
        return serverError("Der opstod en fejl i databasen. Prøv igen om lidt.", model);
    }

    private String notFound(String message, Model model) {
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        model.addAttribute("message", message);
        return "error/404";
    }

    private String serverError(String message, Model model) {
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        model.addAttribute("message", message);
        return "error/500";
    }
}
