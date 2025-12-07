package com.budget.interfaces.dto.response;

import java.util.UUID;

public class LoginResponse {

    public UUID userId;
    public String email;
    public String fullName;
    public String currency;
    public String accessToken;
    public String refreshToken;
    public String tokenType = "Bearer";
    public Long expiresIn;

    public LoginResponse(UUID userId, String email, String fullName, String currency,
                         String accessToken, String refreshToken, Long expiresIn) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.currency = currency;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }
}
