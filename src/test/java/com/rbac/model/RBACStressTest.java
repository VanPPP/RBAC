package com.rbac.model;

import com.rbac.model.*;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RBACStressTest {

    @Test
    public void testConcurrentOperations() throws InterruptedException {
        RBACSystem system = new RBACSystem();
        int threadsCount = 20;
        int operationsPerThread = 100;

        ExecutorService executor = Executors.newFixedThreadPool(threadsCount);
        CountDownLatch latch = new CountDownLatch(threadsCount);

        for (int i = 0; i < threadsCount; i++) {
            final int threadId = i;
            executor.execute(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        String name = "user_" + threadId + "_" + j;
                        // Проверяем UserManager
                        system.getUserManager().add(User.create(name, "Stress Test", "test@test.com"));
                        // Проверяем асинхронный AuditLog
                        AuditLog.log("THREAD_" + threadId, "CREATE", name);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        System.out.println("Stress test completed. Total users: " + system.getUserManager().count());
    }
}