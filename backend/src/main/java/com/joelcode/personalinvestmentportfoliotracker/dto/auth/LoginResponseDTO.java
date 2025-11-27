package com.joelcode.personalinvestmentportfoliotracker.dto.auth;

import com.joelcode.personalinvestmentportfoliotracker.entities.User;

import java.util.List;

public class LoginResponseDTO {

    // Exposes user information to the frontend

    // Login response DTO (output)
    private String token;
    private String tokenType = "Bearer";
    private String username;
    private String fullName;
    private String email;
    private List<User.Role> roles;

    // Constructor
    public LoginResponseDTO(String token, String tokenType, String username, String fullName, String email, List<User.Role> roles) {
        this.token = token;
        this.tokenType = tokenType;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.roles = roles;
    }

    // Getters
    public String getToken() {return token;}

    public String getTokenType() {return tokenType;}

    public String getUsername() {return username;}

    public String getFullName() {return fullName;}

    public String getEmail() {return email;}

    public List<User.Role> getRoles() {return roles;}

}
