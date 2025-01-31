package com.BookMyShow.demo.strategy.PaymentStrategy;


import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Setter
@Component("CASH")
public class CashStrategy implements PaymentStrategy {
    private int amount;

    public CashStrategy(int amount) {
        this.amount = amount;
    }

    @Override
    public void pay(PaymentStrategy strategy, double amount) {
        System.out.println("Pay using Cash");
    }
}
