package org.example.wishlist2.Model;

public class Wishes {
    private Long wishes_id;
    private Long wishlist_id;
    private String title;
    private String description;
    private String img_path;

    public Wishes() {
    }

    public Wishes(String img_path, String description, String title) {
        this.img_path = img_path;
        this.description = description;
        this.title = title;
    }

    public Long getWishes_id() {
        return wishes_id;
    }

    public void setWishes_id(Long wishes_id) {
        this.wishes_id = wishes_id;
    }

    public Long getWishlist_id() {
        return wishlist_id;
    }

    public void setWishlist_id(Long wishlist_id) {
        this.wishlist_id = wishlist_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }
}
