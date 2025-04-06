package org.example.greetingright.dto;

import lombok.Data;

@Data
public class LoginSignupRequestDTO {
    private String username;
    private String password;

    public LoginSignupRequestDTO(String username, String password) {
        this.username = username;
        this.password = password != null ? password : ""; // Default to an empty string if password is null
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
