package org.example.greetingright.dto;

import java.util.Date;
import java.util.Set;

public class UserDTO {
    private String username;
    private Date creationDate;
    private Set<String> roles;

    public UserDTO(String username, Date creationDate, Set<String> roles) {
        this.username = username;
        this.creationDate = creationDate;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}