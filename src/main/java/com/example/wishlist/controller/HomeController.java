package com.example.wishlist.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


// Forside
@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
