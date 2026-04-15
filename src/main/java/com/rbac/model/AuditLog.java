package com.rbac.model;

import com.rbac.model.LogEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class AuditLog {
    private static final List<LogEntry> logs = new CopyOnWriteArrayList<>();
    private static final BlockingQueue<LogEntry> logQueue = new LinkedBlockingQueue<>();

    static {
        Thread worker = new Thread(() -> {
            try {
                while (true) {
                    LogEntry entry = logQueue.take(); // Ждет появления лога
                    logs.add(entry);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        worker.setDaemon(true);
        worker.start();
    }

    public static void log(String actor, String action, String details) {
        logQueue.offer(new LogEntry(
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