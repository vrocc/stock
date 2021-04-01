package com.roc.financial;

import java.util.concurrent.CountDownLatch;
import java.util.function.IntConsumer;

class ZeroEvenOdd1 {

    private volatile int n;

    private CountDownLatch countDownLatchA = new CountDownLatch(0); // zero 为0 ，因为最开始打印不能让它等待
    private CountDownLatch countDownLatchB = new CountDownLatch(1); // 控制打印奇数的顺序
    private CountDownLatch countDownLatchC = new CountDownLatch(1); // 控制打印偶数的顺序

    public ZeroEvenOdd1(int n) {
        this.n = n;
    }

    public static void main(String[] args) {
        ZeroEvenOdd1 odd = new ZeroEvenOdd1(5);
        IntConsumer intConsumer = System.out::print;

        new Thread(() -> {
            try {
                odd.odd(intConsumer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();


        new Thread(() -> {
            try {
                odd.even(intConsumer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                odd.zero(intConsumer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }

    // printNumber.accept(x) outputs "x", where x is an integer.
    public void zero(IntConsumer printNumber) throws InterruptedException {
        for(int i =0; i < n; i++) {
            countDownLatchA.await();
            printNumber.accept(0);
            countDownLatchA = new CountDownLatch(1);
            if(i % 2 == 0) {
                countDownLatchB.countDown(); // 激活打印奇数的方法
            }else {
                countDownLatchC.countDown();
            }
        }
    }

    public void odd(IntConsumer printNumber) throws InterruptedException {
        for (int i = 1; i <= n; i=i+2) {
            countDownLatchB.await();
            printNumber.accept(i);
            countDownLatchB = new CountDownLatch(1);
            countDownLatchA.countDown();
        }
    }

    public void even(IntConsumer printNumber) throws InterruptedException {
        for (int i = 2; i <= n; i=i+2) {
            countDownLatchC.await();
            printNumber.accept(i);
            countDownLatchC = new CountDownLatch(1);
            countDownLatchA.countDown();
        }
    }
}