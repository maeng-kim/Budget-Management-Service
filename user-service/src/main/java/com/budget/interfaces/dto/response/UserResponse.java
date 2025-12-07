package com.budget.interfaces.dto.response;

import com.budget.domain.User;

import java.util.UUID;

public class UserResponse {

    public UUID id;
    public String email;
    public String fullName;
    public String currency;
    public String createdAt;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail().getValue();
        this.fullName = user.getFullName();
        this.currency = user.getCurrency();
        this.createdAt = user.getCreatedAt().toString();
    }
}
