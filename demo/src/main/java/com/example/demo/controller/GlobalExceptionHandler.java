package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProfileDontExist.class)
    @ResponseStatus(Http.Status.NOT_FOUND)
    public String profileNotFound(ProfileDontExist pde, Model model){
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        model.addAttribute("message", pde.getMessage());
        return "error/404"
    }

    @ExceptionHandler(DatabaseExceptionHandler.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleDatabaseOperation(DatabaseExceptionHandler dbe, Model model) {
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        model.addAttribute("message", dbe.getLocalizedMessage());
        return "error/500";
    }

//    400 (Bad Request)
//    401 (Unauthorized Error)
//    403 (Forbidden)
//    404 (Not Found)
//    500 (Internal Server Error)
}