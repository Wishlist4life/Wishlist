package com.example.demo.model;

public class Wishlist {
    private Integer id;
    private Integer userId;
    private String name;
    private String description;
    private String shareToken;

    public Wishlist() {
    }

    public Wishlist(Integer userId, String name, String description, String shareToken) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.shareToken = shareToken;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShareToken() {
        return shareToken;
    }

    public void setShareToken(String shareToken) {
        this.shareToken = shareToken;
    }
}
