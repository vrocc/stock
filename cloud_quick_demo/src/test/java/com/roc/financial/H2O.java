package com.roc.financial;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class H2O {
    private volatile AtomicInteger integer = new AtomicInteger(2);
    private volatile AtomicBoolean showed = new AtomicBoolean(false);
    private volatile CyclicBarrier barrier = new CyclicBarrier(2);

    private volatile CountDownLatch latch = new CountDownLatch(3);

    private volatile Semaphore semaphore2 = new Semaphore(2);
    private volatile Semaphore semaphore1 = new Semaphore(1);


    public H2O() {
        // "OOHHHH"
    }

    public static void main(String[] args) throws InterruptedException {
        for (int j = 0; j < 100; j++) {
            String str = "OOHHHH";
            H2O h2O = new H2O();
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                new Thread(() -> {
                    if (c == 'H') {
                        try {
                            h2O.hydrogen(() -> {
                                System.out.print(c);
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (c == 'O') {
                        try {
                            h2O.oxygen(() -> {
                                System.out.print(c);
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
            Thread.sleep(50);
            System.out.println();
        }
    }

    public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {

        // releaseHydrogen.run() outputs "H". Do not change or remove this line.
        semaphore2.acquire();
        releaseHydrogen.run();

    }

    public void oxygen(Runnable releaseOxygen) throws InterruptedException {

        // releaseOxygen.run() outputs "O". Do not change or remove this line.
        semaphore1.acquire();
        releaseOxygen.run();
        while (semaphore2.availablePermits() != 0) {
            Thread.yield();
        }
        semaphore2.release(2);
        semaphore1.release();
    }
}