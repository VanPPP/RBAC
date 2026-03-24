package com.rbac.model;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class ThreadSimulator {

    private static final int THREADS_COUNT = 4;
    private static final int CALCULATION_STEPS = 25;
    private static final int STEP_DELAY_MS = 200;
    private static final int BAR_WIDTH = 20;

    public static void runSimulation() {
        AtomicIntegerArray progress = new AtomicIntegerArray(THREADS_COUNT);
        long[] threadIds = new long[THREADS_COUNT];
        long[] durations = new long[THREADS_COUNT];
        boolean[] isFinished = new boolean[THREADS_COUNT];

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch allDone = new CountDownLatch(THREADS_COUNT);
        ExecutorService executor = Executors.newFixedThreadPool(THREADS_COUNT);

        for (int i = 0; i < THREADS_COUNT; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    threadIds[index] = Thread.currentThread().threadId();

                    long startTime = System.currentTimeMillis();

                    for (int step = 1; step <= CALCULATION_STEPS; step++) {
                        progress.set(index, step);
                        Thread.sleep(STEP_DELAY_MS + (int)(Math.random() * 100));
                    }

                    durations[index] = System.currentTimeMillis() - startTime;
                    isFinished[index] = true;
                    allDone.countDown();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        startLatch.countDown();

        while (allDone.getCount() > 0) {
            drawUI(progress, threadIds, isFinished, durations);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }

        drawUI(progress, threadIds, isFinished, durations);

        executor.shutdown();
        System.out.println("\nAll calculations finished.");
    }

    private static synchronized void drawUI(AtomicIntegerArray progress, long[] ids, boolean[] finished, long[] durations) {
        System.out.print("\033[H\033[2J");
        System.out.flush();

        StringBuilder sb = new StringBuilder();
        sb.append("Multi-threaded Calculation Progress:\n\n");

        for (int i = 0; i < THREADS_COUNT; i++) {
            int currentStep = progress.get(i);
            int percent = (currentStep * 100) / CALCULATION_STEPS;

            StringBuilder bar = new StringBuilder("[");
            int filledCount = (currentStep * BAR_WIDTH) / CALCULATION_STEPS;
            for (int k = 0; k < BAR_WIDTH; k++) {
                bar.append(k < filledCount ? "#" : " ");
            }
            bar.append("]");

            String line = String.format("Thread #%d | ID: %-5d | %s %3d%%",
                    (i + 1), ids[i], bar.toString(), percent);

            if (finished[i]) {
                line += String.format(" | Time: %d ms", durations[i]);
            }

            sb.append(line).append("\n");
        }

        System.out.print(sb.toString());
    }
}