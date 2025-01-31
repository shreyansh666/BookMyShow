package com.BookMyShow.demo.strategy.PaymentStrategy;

public interface PaymentStrategy {
    void pay(PaymentStrategy strategy, double amount);
}
