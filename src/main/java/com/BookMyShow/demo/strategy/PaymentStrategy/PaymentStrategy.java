package com.BookMyShow.demo.strategy.PaymentStrategy;

public interface PaymentStrategy {
    boolean pay(PaymentStrategy strategy, double amount);
}
