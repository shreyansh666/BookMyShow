package com.BookMyShow.demo.strategy.PaymentStrategy;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Setter
@Component("CARD")
public class CreditCard implements PaymentStrategy {
        private String cardNumber;
        private String cardHolderName;

        @Override
        public boolean pay(PaymentStrategy strategy, double amount) {
            System.out.println("Pay using Card");
            return true;
        }
 }
