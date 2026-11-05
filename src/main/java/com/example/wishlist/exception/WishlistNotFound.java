package com.example.wishlist.exception;

/** Seddel eller ønske findes ikke eller tilhører en anden. */
public class WishlistNotFound extends RuntimeException {
    public WishlistNotFound(int id) {
        super("Wishlist with id: " + id + " was not found");
    }

    public WishlistNotFound(String message) {
        super(message);
    }
}
