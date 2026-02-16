package com.rbac.model;

public record User(String username, String fullName, String email) {

    private static final String USERNAME_PATTERN = "[a-zA-Z0-9_]{3,20}";
    private static final String EMAIL_PATTERN = ".+@.+\\..+";

    public static User create(String username, String fullName, String email) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (!username.matches(USERNAME_PATTERN)) {
            throw new IllegalArgumentException("Username must be 3-20 chars: letters, numbers, underscore only");
        }

        if (!email.matches(EMAIL_PATTERN)) {
            throw new IllegalArgumentException("Email must contain @ and a dot after @");
        }

        return new User(username, fullName, email);
    }

    public String format() {
        return username + " (" + fullName + ") <" + email + ">";
    }
}