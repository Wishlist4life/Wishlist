package com.example.wishlist.controller;

import com.example.wishlist.config.AppUserPrincipal;
import com.example.wishlist.exception.ReservationException;
import com.example.wishlist.model.Wishlist;
import com.example.wishlist.service.WishlistService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Delt seddel og reservation. */
@Controller
public class ShareController {

    private final WishlistService service;

    public ShareController(WishlistService service) {
        this.service = service;
    }

    @GetMapping("/share/{token}")
    public String shared(@AuthenticationPrincipal AppUserPrincipal principal,
                         @PathVariable String token,
                         Model model) {
        Wishlist wishlist = service.getSharedWishlist(token);

        model.addAttribute("wishlist", wishlist);
        model.addAttribute("ownerName", service.getOwnerName(wishlist));
        model.addAttribute("wishes", service.findWishes(wishlist.getId()));
        model.addAttribute("isOwner", principal != null && principal.getId() == wishlist.getUserId());
        return "shared";
    }

    @PostMapping("/share/{token}/wishes/{wishId}/reserve")
    public String reserve(@AuthenticationPrincipal AppUserPrincipal principal,
                          @PathVariable String token,
                          @PathVariable int wishId,
                          RedirectAttributes redirect) {
        try {
            service.reserve(token, wishId, principal.getId());
        } catch (ReservationException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/share/" + token;
    }

    @PostMapping("/share/{token}/wishes/{wishId}/unreserve")
    public String unreserve(@AuthenticationPrincipal AppUserPrincipal principal,
                            @PathVariable String token,
                            @PathVariable int wishId) {
        service.unreserve(token, wishId, principal.getId());
        return "redirect:/share/" + token;
    }
}
