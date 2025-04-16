package org.example.greetingright.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "userID", nullable = false, unique = true)
    private Long id;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @ElementCollection
    @CollectionTable(name = "user_dataset_wishes", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "datasetWishID")
    private Set<Long> datasetWishIDs = new HashSet<>();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date", nullable = false, updatable = false)
    private Date creationDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "userRole",
            joinColumns = @JoinColumn(name = "userID"),
            inverseJoinColumns = @JoinColumn(name = "roleID")
    )
    private Set<Role> roles;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private RefreshToken refreshToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Wish> wishes = new HashSet<>();

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Wish> getWishes() {
        return wishes;
    }

    public void setWishes(Set<Wish> wishes) {
        this.wishes = wishes;
    }

    public Set<Long> getDatasetWishIDs() {
        return datasetWishIDs;
    }

    public void setDatasetWishIDs(Set<Long> datasetWishIDs) {
        this.datasetWishIDs = datasetWishIDs;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    @PrePersist
    protected void onCreate() {
        this.creationDate = new Date();
    }
}

