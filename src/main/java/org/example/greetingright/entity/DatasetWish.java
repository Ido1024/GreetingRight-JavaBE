package org.example.greetingright.entity;

import jakarta.persistence.*;

@Entity
public class DatasetWish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "datasetWishID", nullable = false)
    private Long datasetWishID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDatasetWishID() {
        return datasetWishID;
    }

    public void setDatasetWishID(Long datasetWishID) {
        this.datasetWishID = datasetWishID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}