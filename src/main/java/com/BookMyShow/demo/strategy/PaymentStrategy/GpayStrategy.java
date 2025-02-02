package com.BookMyShow.demo.strategy.PaymentStrategy;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Setter
@AllArgsConstructor
@Component("GPAY")
public class GpayStrategy implements PaymentStrategy {
    private String upiId;

    @Override
    public boolean pay(PaymentStrategy strategy, double amount) {
        System.out.println("Pay using Google Pay");
        return true;
    }
}
