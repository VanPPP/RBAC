package com.rbac.model;

import com.rbac.model.DateUtils;

public record LogEntry(
        String timestamp,
        String actor,
        String action,
        String details
) {
    @Override
    public String toString() {
        return String.format("[%s] [%s] %s: %s", timestamp, actor, action, details);
    }
}