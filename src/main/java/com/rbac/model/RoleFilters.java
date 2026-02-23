package com.rbac.model;

public class RoleFilters {

    public static RoleFilter byName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Role name cannot be empty");
        }
        return role -> role.getName().equals(name);
    }

    public static RoleFilter byNameContains(String substring) {
        if (substring == null || substring.isBlank()) {
            throw new IllegalArgumentException("Search text cannot be empty");
        }
        return role -> role.getName().toLowerCase().contains(substring.toLowerCase());
    }

    public static RoleFilter hasPermission(Permission permission) {
        if (permission == null) {
            throw new IllegalArgumentException("Permission cannot be null");
        }
        return role -> role.hasPermission(permission);
    }

    public static RoleFilter hasPermission(String permissionName, String resource) {
        if (permissionName == null || permissionName.isBlank()) {
            throw new IllegalArgumentException("Permission name cannot be empty");
        }
        if (resource == null || resource.isBlank()) {
            throw new IllegalArgumentException("Resource cannot be empty");
        }
        return role -> role.hasPermission(permissionName, resource);
    }

    public static RoleFilter hasAtLeastNPermissions(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Number of permissions cannot be negative");
        }
        return role -> role.getPermissions().size() >= n;
    }
}