package org.example.wishlist2.Model;

public class Wishlist {

    private Long wishlist_id;
    private Long userId;
    private String wishlist_title;
    private String description;
    private String share_url;

    public Wishlist() {
    }

    public Wishlist(Long userId, String name, String description, String share_url) {
        this.userId = userId;
        this.wishlist_title = name;
        this.description = description;
        this.share_url = share_url;
    }

    public Long getWishlist_id() {
        return wishlist_id;
    }

    public void setWishlist_id(Long wishlist_id) {
        this.wishlist_id = wishlist_id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return wishlist_title;
    }

    public void setName(String name) {
        this.wishlist_title = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShareToken() {
        return share_url;
    }

    public void setShareToken(String shareToken) {
        this.share_url = shareToken;
    }
}
