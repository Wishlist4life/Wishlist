package com.example.wishlist.controller;

import com.example.wishlist.config.AppUserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** loggedIn og currentUserId til alle sider. */
@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("loggedIn")
    public boolean loggedIn(@AuthenticationPrincipal AppUserPrincipal principal) {
        return principal != null;
    }

    @ModelAttribute("currentUserId")
    public Integer currentUserId(@AuthenticationPrincipal AppUserPrincipal principal) {
        return principal == null ? null : principal.getId();
    }
}
