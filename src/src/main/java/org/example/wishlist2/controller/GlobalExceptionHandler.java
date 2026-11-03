package org.example.wishlist2.controller;

import org.example.wishlist2.exception.DatabaseExceptionHandler;
import org.example.wishlist2.exception.UserNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String profileNotFound(UserNotFound userNotFound, Model model){
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        model.addAttribute("message", userNotFound.getMessage()); //forskel på getmsg og getlocalized?
        return "error/404";
    }

    @ExceptionHandler(DatabaseExceptionHandler.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleDatabaseOperation(DatabaseExceptionHandler databaseExceptionHandler, Model model) {
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        model.addAttribute("message", databaseExceptionHandler.getLocalizedMessage()); //forskel på getmsg og getlocalized bibop
        return "error/500";
    }

//    400 (Bad Request)
//    401 (Unauthorized Error)
//    403 (Forbidden)
//    404 (Not Found)
//    500 (Internal Server Error)
}