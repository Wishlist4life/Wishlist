package com.example.wishlist.controller;

import com.example.wishlist.exception.DatabaseOperationException;
import com.example.wishlist.exception.UserNotFound;
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
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        model.addAttribute("message", pde.getMessage()); //forskel på getmsg og getlocalized?
        return "error/404";
    }

    @ExceptionHandler(DatabaseOperationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleDatabaseOperation(DatabaseOperationException dbe, Model model) {
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        model.addAttribute("message", dbe.getLocalizedMessage()); //forskel på getmsg og getlocalized bibop
        return "error/500";
    }

//    400 (Bad Request)
//    401 (Unauthorized Error)
//    403 (Forbidden)
//    404 (Not Found)
//    500 (Internal Server Error)
}
