package com.example.wishlist.controller;

import com.example.wishlist.config.AppUserPrincipal;
import com.example.wishlist.exception.WishValidation;
import com.example.wishlist.model.Wishlist;
import com.example.wishlist.service.WishlistService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/** Alt der kraever login: egne oenskesedler og oensker. */
@Controller
public class WishlistController {

    private final WishlistService service;

    public WishlistController(WishlistService service) {
        this.service = service;
    }

    @GetMapping("/wishlists")
    public String wishlists(@AuthenticationPrincipal AppUserPrincipal principal, Model model) {
        model.addAttribute("userName", principal.getDisplayName());
        model.addAttribute("wishlists", service.findWishlists(principal.getId()));
        return "wishlists";
    }

    @PostMapping("/wishlists")
    public String createWishlist(@AuthenticationPrincipal AppUserPrincipal principal,
                                 @RequestParam String name,
                                 @RequestParam(required = false) String description,
                                 RedirectAttributes redirect) {
        try {
            Wishlist created = service.createWishlist(principal.getId(), name, description);
            return "redirect:/wishlists/" + created.getId();
        } catch (WishValidation e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/wishlists";
        }
    }

    @GetMapping("/wishlists/{id}")
    public String wishlist(@AuthenticationPrincipal AppUserPrincipal principal,
                           @PathVariable int id,
                           Model model) {
        Wishlist wishlist = service.getOwnWishlist(id, principal.getId());
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        model.addAttribute("wishlist", wishlist);
        model.addAttribute("wishes", service.findWishes(id));
        model.addAttribute("shareUrl", baseUrl + "/share/" + wishlist.getShareToken());
        return "wishlist";
    }

    @PostMapping("/wishlists/{id}/delete")
    public String deleteWishlist(@AuthenticationPrincipal AppUserPrincipal principal, @PathVariable int id) {
        service.deleteWishlist(id, principal.getId());
        return "redirect:/wishlists";
    }

    @PostMapping("/wishlists/{id}/wishes")
    public String addWish(@AuthenticationPrincipal AppUserPrincipal principal,
                          @PathVariable int id,
                          @RequestParam String name,
                          @RequestParam(required = false) String description,
                          @RequestParam(required = false) String price,
                          @RequestParam(required = false) String link,
                          RedirectAttributes redirect) {
        try {
            service.addWish(id, principal.getId(), name, description, price, link);
        } catch (WishValidation e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/wishlists/" + id;
    }

    @PostMapping("/wishlists/{id}/wishes/{wishId}/delete")
    public String deleteWish(@AuthenticationPrincipal AppUserPrincipal principal,
                             @PathVariable int id,
                             @PathVariable int wishId) {
        service.deleteWish(id, wishId, principal.getId());
        return "redirect:/wishlists/" + id;
    }
}
