package com.example.wishlist.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


// Simpel controller der håndterer GET requests til roden af applikationen og returnerer "index" viewet.
@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
}

