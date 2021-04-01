package com.roc.financial;

import java.util.function.IntConsumer;

public class ZeroEvenOdd {
    private int n;

    private volatile boolean flag = true;
    private volatile boolean sw = true;

    public ZeroEvenOdd(int n) {
        this.n = n;
    }

    public static void main(String[] args) {
        ZeroEvenOdd odd = new ZeroEvenOdd(9);
        IntConsumer intConsumer = System.out::print;


        new Thread(() -> {
            try {
                odd.zero(intConsumer);
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
                odd.odd(intConsumer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }

    // printNumber.accept(x) outputs "x", where x is an integer.
    public void zero(IntConsumer printNumber) throws InterruptedException {
        for (int i = 1; i <= n; i++) {
            while (!flag) {
                Thread.yield();
            }
            printNumber.accept(0);
            flag = false;
        }
    }

    public void even(IntConsumer printNumber) throws InterruptedException {
        for (int i = 1; i <= n; i++) {
            if (i % 2 == 0) {
                while (!(!flag && !sw)) {
                    Thread.yield();
                }
                printNumber.accept(i);
                flag = true;
                sw = true;
            }
        }
    }

    public void odd(IntConsumer printNumber) throws InterruptedException {
        for (int i = 1; i <= n; i++) {
            if (i % 2 == 1) {
                while (!(!flag && sw)) {
                    Thread.yield();
                }
                printNumber.accept(i);
                flag = true;
                sw = false;
            }
        }
    }

//    private void sout(Supplier<Boolean> expression, IntConsumer intConsumer, int value) {
//        while (!expression.get()) {
//            Thread.yield();
//        }
//        intConsumer.accept(value);
//    }
}