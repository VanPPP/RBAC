package com.rbac.model;

public class UserFilters {

    public static UserFilter byUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Search username cannot be empty");
        }
        return user -> user.username().equals(username);
    }

    public static UserFilter byUsernameContains(String substring) {
        if (substring == null || substring.isBlank()) {
            throw new IllegalArgumentException("Search text cannot be empty");
        }
        return user -> user.username().toLowerCase().contains(substring.toLowerCase());
    }

    public static UserFilter byEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        return user -> user.email().equals(email);
    }

    public static UserFilter byEmailDomain(String domain) {
        if (domain == null || domain.isBlank()) {
            throw new IllegalArgumentException("Domain cannot be empty");
        }
        return user -> user.email().toLowerCase().endsWith(domain.toLowerCase());
    }

    public static UserFilter byFullNameContains(String substring) {
        if (substring == null || substring.isBlank()) {
            throw new IllegalArgumentException("Name fragment cannot be empty");
        }
        return user -> user.fullName().toLowerCase().contains(substring.toLowerCase());
    }
}