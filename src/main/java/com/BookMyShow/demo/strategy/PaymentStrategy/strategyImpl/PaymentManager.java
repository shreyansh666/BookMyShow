package com.BookMyShow.demo.strategy.PaymentStrategy.strategyImpl;


import com.BookMyShow.demo.enums.PaymentType;
import com.BookMyShow.demo.strategy.PaymentStrategy.PaymentStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class PaymentManager {

    private final Map<String, PaymentStrategy> strategies;

    @Autowired
    public PaymentManager(Map<String, PaymentStrategy> strategies) {
        this.strategies = strategies;
    }

    public boolean pay(PaymentType paymentType, double amount) throws Exception {
        PaymentStrategy strategy = strategies.get(paymentType.name());
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported Payment type: " + paymentType);
        }

        return strategy.pay(strategy,amount);
    }

//    public boolean pay(PaymentStrategy paymentStrategy, double amount) {
//        if (paymentStrategy == null) {
//            throw new IllegalStateException("Define a Payment Strategy.");
//        }
//           paymentStrategy.pay(paymentStrategy, amount);
//        return true;
//
//        }
    }
