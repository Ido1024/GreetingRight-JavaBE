package org.example.greetingright.dto;

import java.util.Date;

public class WishDTO {
    private Long id;
    private String birthdayWish;
    private Date creationDate;
    private boolean isFavorite;

    // Constructor
    public WishDTO(Long id, String birthdayWish, Date creationDate, boolean isFavorite) {
        this.id = id;
        this.birthdayWish = birthdayWish;
        this.creationDate = creationDate;
        this.isFavorite = isFavorite;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBirthdayWish() {
        return birthdayWish;
    }

    public void setBirthdayWish(String birthdayWish) {
        this.birthdayWish = birthdayWish;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}