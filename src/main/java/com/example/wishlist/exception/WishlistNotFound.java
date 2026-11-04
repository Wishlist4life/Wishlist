package com.example.wishlist.exception;

/** Oenskesedlen (eller oensket) findes ikke, eller tilhoerer ikke den bruger der spoerger. */
public class WishlistNotFound extends RuntimeException {
    public WishlistNotFound(int id) {
        super("Wishlist with id: " + id + " was not found");
    }

    public WishlistNotFound(String message) {
        super(message);
    }
}
