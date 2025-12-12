package com.joelcode.personalinvestmentportfoliotracker.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class UserCreateRequest {

    // User creation request DTO (input)
    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email")
    private String email;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    //  Constructor
    public UserCreateRequest(String email, String username, String password, String fullName) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
    }

    public UserCreateRequest() {}

    //Getters and setters
    public String getEmail() {return email;}

    public String getUsername() {return username;}

    public String getPassword() {return password;}

    public void setEmail(String email) {this.email = email;}

    public void setUsername(String username) {this.username = username;}

    public void setPassword (String password) {this.password = password;}

    public String getFullName() {return fullName;}

    public void setFullName(String fullName) {this.fullName = fullName;}
}
