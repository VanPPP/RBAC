package com.rbac.model;

import java.util.*;

public class Role {
    private final String id;
    private final String name;
    private String description;
    private final Set<Permission> permissions;

    public Role(String name, String description) {
        this(name, description, new HashSet<>());
    }

    public Role(String name, String description, Set<Permission> permissions) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Role name cannot be empty");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Role description cannot be empty");
        }

        this.id = "role_" + UUID.randomUUID();
        this.name = name.trim();
        this.description = description.trim();
        this.permissions = new HashSet<>(permissions);
    }

    public void addPermission(Permission permission) {
        if (permission != null) {
            permissions.add(permission);
        }
    }

    public void removePermission(Permission permission) {
        permissions.remove(permission);
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }

    public boolean hasPermission(String permissionName, String resource) {
        return permissions.stream()
                .anyMatch(p -> p.matches(permissionName, resource));
    }

    public Set<Permission> getPermissions() {
        return Collections.unmodifiableSet(new HashSet<>(permissions));
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }

    public void setDescription(String description) {
        if (description != null && !description.isBlank()) {
            this.description = description.trim();
        }
    }

    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("Role: ").append(name).append(" [ID: ").append(id).append("]\n");
        sb.append("Description: ").append(description).append("\n");
        sb.append("Permissions (").append(permissions.size()).append("):\n");

        if (permissions.isEmpty()) {
            sb.append(" - No permissions assigned");
        } else {
            permissions.stream()
                    .sorted((p1, p2) -> {
                        int cmp = p1.resource().compareTo(p2.resource());
                        return cmp != 0 ? cmp : p1.name().compareTo(p2.name());
                    })
                    .forEach(p -> sb.append(" - ").append(p.format()).append("\n"));
            sb.deleteCharAt(sb.length() - 1); // убираем последний \n
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return format();
    }
}