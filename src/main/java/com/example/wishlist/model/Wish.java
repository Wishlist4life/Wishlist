package com.example.wishlist.model;

import java.math.BigDecimal;

public class Wish {
    private Integer id;
    private Integer wishlistId;
    private String name;
    private String description;
    private BigDecimal price;
    private String link;
    /** Bruger-id på den der har reserveret, ellers null. */
    private Integer reservedBy;

    public Wish() {
    }

    public Wish(Integer wishlistId, String name, String description, BigDecimal price, String link) {
        this.wishlistId = wishlistId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.link = link;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(Integer wishlistId) {
        this.wishlistId = wishlistId;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Integer getReservedBy() {
        return reservedBy;
    }

    public void setReservedBy(Integer reservedBy) {
        this.reservedBy = reservedBy;
    }

    public boolean isReserved() {
        return reservedBy != null;
    }
}
