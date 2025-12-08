package com.budget.infrastructure.security;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    private static final int ITERATIONS = 10000;
    private static final int SALT_LENGTH = 16;

    public static String hashPassword(String password, String salt) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            String saltedPassword = password + salt;
            byte[] hash = saltedPassword.getBytes("UTF-8");

            for (int i = 0; i < ITERATIONS; i++) {
                hash = md.digest(hash);
            }

            return Base64.getEncoder().encodeToString(hash);

        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public static boolean verifyPassword(String password, String salt, String hash) {
        String computedHash = hashPassword(password, salt);
        return computedHash.equals(hash);
    }

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String[] hashWithNewSalt(String password) {
        String salt = generateSalt();
        String hash = hashPassword(password, salt);
        return new String[]{salt, hash};
    }

}
