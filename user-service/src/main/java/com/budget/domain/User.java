package com.budget.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import com.budget.infrastructure.security.PasswordUtil;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User extends PanacheEntityBase {

    @Id
    private String id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email", unique = true, nullable = false))
    private Email email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "password_salt", nullable = false)
    private String passwordSalt;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "currency", nullable = false)
    private String currency = "Euro";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected User() {}

    public User(Email email, String rawPassword, String fullName) {

        String[] saltAndHash = PasswordUtil.hashWithNewSalt(rawPassword);

        this.id = UUID.randomUUID().toString();
        this.email = email;
        this.passwordSalt = saltAndHash[0];
        this.passwordHash = saltAndHash[1];
        this.fullName = fullName;
        this.createdAt = LocalDateTime.now();
    }

    public boolean authenticate(String rawPassword) {
        return PasswordUtil.verifyPassword(rawPassword, this.passwordSalt, this.passwordHash);
    }

    public void changePassword(String currentPassword, String newPassword) {
        if (!authenticate(currentPassword)) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }

        // Generate new salt and hash
        String[] saltAndHash = PasswordUtil.hashWithNewSalt(newPassword);
        this.passwordSalt = saltAndHash[0];
        this.passwordHash = saltAndHash[1];
    }

    public void updateProfile(String fullName, String currency) {
        if (fullName != null && !fullName.isBlank()) {
            this.fullName = fullName;
        }
        if (currency != null && !currency.isBlank()) {
            this.currency = currency;
        }
    }

    public static User findByEmail(String email) {
        return find("email", email).firstResult();
    }

    public static boolean existsByEmail(String email) {
        return count("email", email) > 0;
    }

    public String getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getCurrency() {
        return currency;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
