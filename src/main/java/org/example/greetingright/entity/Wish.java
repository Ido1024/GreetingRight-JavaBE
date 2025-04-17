package org.example.greetingright.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "birthday_wish", columnDefinition = "TEXT") // make sure to use TEXT for large text
    private String birthdayWish;
    private Date creationDate;
    private boolean isFavorite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
