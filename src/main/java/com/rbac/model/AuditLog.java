package com.rbac.model;

import com.rbac.model.LogEntry;
import java.util.ArrayList;
import java.util.List;

public class AuditLog {
    private static final List<LogEntry> logs = new ArrayList<>();

    public static void log(String actor, String action, String details) {
        logs.add(new LogEntry(
                DateUtils.getCurrentDateTime(),
                actor,
                action,
                details
        ));
    }

    public static List<LogEntry> getLogs() {
        return new ArrayList<>(logs);
    }
}