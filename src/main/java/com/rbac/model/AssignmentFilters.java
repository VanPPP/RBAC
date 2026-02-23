package com.rbac.model;

import java.time.Instant;
import java.time.LocalDate;

public class AssignmentFilters {

    public static AssignmentFilter byUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return assignment -> assignment.user().equals(user);
    }

    public static AssignmentFilter byUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        return assignment -> assignment.user().username().equals(username);
    }

    public static AssignmentFilter byRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        return assignment -> assignment.role().equals(role);
    }

    public static AssignmentFilter byRoleName(String roleName) {
        if (roleName == null || roleName.isBlank()) {
            throw new IllegalArgumentException("Role name cannot be empty");
        }
        return assignment -> assignment.role().getName().equals(roleName);
    }

    public static AssignmentFilter activeOnly() {
        return RoleAssignment::isActive;
    }

    public static AssignmentFilter inactiveOnly() {
        return assignment -> !assignment.isActive();
    }

    public static AssignmentFilter byType(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Type cannot be empty");
        }
        if (!type.equals("PERMANENT") && !type.equals("TEMPORARY")) {
            throw new IllegalArgumentException("Type must be PERMANENT or TEMPORARY");
        }
        return assignment -> assignment.assignmentType().equals(type);
    }

    public static AssignmentFilter assignedBy(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        return assignment -> assignment.metadata().assignedBy().equals(username);
    }

    public static AssignmentFilter assignedAfter(String date) {
        if (date == null || date.isBlank()) {
            throw new IllegalArgumentException("Date cannot be empty");
        }
        return assignment -> {
            String assignedAt = assignment.metadata().assignedAt();
            return assignedAt.compareTo(date) > 0;
        };
    }

    public static AssignmentFilter expiringBefore(String date) {
        if (date == null || date.isBlank()) {
            throw new IllegalArgumentException("Date cannot be empty");
        }
        return assignment -> {
            if (!(assignment instanceof TemporaryAssignment)) {
                return false;
            }
            TemporaryAssignment temp = (TemporaryAssignment) assignment;
            return temp.getExpiresAt().compareTo(date) < 0;
        };
    }
}