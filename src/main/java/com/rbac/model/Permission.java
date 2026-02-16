package com.rbac.model;

public record Permission(String name, String resource, String description) {

    public Permission {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Permission name cannot be empty");
        }
        if (resource == null || resource.isBlank()) {
            throw new IllegalArgumentException("Resource cannot be empty");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        if (name.contains(" ")) {
            throw new IllegalArgumentException("Permission name cannot contain spaces");
        }

        // Нормализация
        name = name.toUpperCase().trim();
        resource = resource.toLowerCase().trim();
        description = description.trim();
    }

    public String format() {
        return name + " on " + resource + ": " + description;
    }

    public boolean matches(String namePattern, String resourcePattern) {
        boolean nameMatches = (namePattern == null) ||
                name.toLowerCase().contains(namePattern.toLowerCase());
        boolean resourceMatches = (resourcePattern == null) ||
                resource.contains(resourcePattern.toLowerCase());
        return nameMatches && resourceMatches;
    }
}