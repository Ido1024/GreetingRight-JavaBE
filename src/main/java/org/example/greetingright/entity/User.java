package org.example.greetingright.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userID", nullable = false, unique = true)
    private Long id;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    // Store both date and time (timestamp) in the database
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date", nullable = false, updatable = false)
    private Date creationDate;

    // Load roles immediately when user is loaded
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DatasetWish> datasetWishes = new HashSet<>();

    // Automatically sets the creation date before inserting the entity into the database
    @PrePersist
    protected void onCreate() {
        this.creationDate = new Date();
    }

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

    public Set<DatasetWish> getDatasetWishes() {
        return datasetWishes;
    }

    public void setDatasetWishes(Set<DatasetWish> datasetWishes) {
        this.datasetWishes = datasetWishes;
    }

    public Date getCreationDate() {
        return creationDate;
    }
}