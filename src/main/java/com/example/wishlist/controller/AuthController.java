package com.example.wishlist.controller;

import com.example.wishlist.exception.EmailException;
import com.example.wishlist.exception.PasswordException;
import com.example.wishlist.exception.UserAlreadyExist;
import com.example.wishlist.exception.UserValidation;
import com.example.wishlist.model.User;
import com.example.wishlist.service.WishlistService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Login-siden og registrering. Selve login-POST'en haandteres af Spring Security (se SecurityConfig). */
@Controller
public class AuthController {

    private final WishlistService service;

    public AuthController(WishlistService service) {
        this.service = service;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String password,
                           Model model) {
        try {
            service.create(new User(name, email, password));
            return "redirect:/login?registered";
        } catch (UserValidation | EmailException | PasswordException | UserAlreadyExist e) {
            // Vis formularen igen med fejlen. Kodeordet sendes bevidst ikke tilbage.
            model.addAttribute("error", e.getMessage());
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            return "register";
        }
    }
}
