package com.joelcode.personalinvestmentportfoliotracker.dto.auth;

public class AuthResponseDTO {

    // Authentication response DTO (output, immutable)
    private String token;
    private String tokenType = "Bearer";
    private String username;
    private String email;

    // Constructors
    public AuthResponseDTO(String token, String tokenType, String username, String email) {
        this.token = token;
        this.tokenType = tokenType;
        this.username = username;
        this.email = email;
    }

    public AuthResponseDTO() {}

    // Getters
    public String getToken() {return token;}

    public String getTokenType() {return tokenType;}

    public String getUsername() {return username;}

    public String getEmail() {return email;}
}
