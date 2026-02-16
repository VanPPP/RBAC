package com.rbac.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record AssignmentMetadata(String assignedBy, String assignedAt, String reason) {

    public AssignmentMetadata {
        if (assignedBy == null || assignedBy.isBlank()) {
            throw new IllegalArgumentException("AssignedBy cannot be empty");
        }
        if (assignedAt == null || assignedAt.isBlank()) {
            throw new IllegalArgumentException("AssignedAt cannot be empty");
        }
    }

    public static AssignmentMetadata now(String assignedBy, String reason) {
        String at = Instant.now().toString();
        return new AssignmentMetadata(assignedBy, at, reason);
    }

    public static AssignmentMetadata now(String assignedBy) {
        return now(assignedBy, null);
    }

    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("Assigned by: ").append(assignedBy).append(" at ").append(assignedAt);
        if (reason != null && !reason.isBlank()) {
            sb.append(" (Reason: ").append(reason).append(")");
        }
        return sb.toString();
    }
}