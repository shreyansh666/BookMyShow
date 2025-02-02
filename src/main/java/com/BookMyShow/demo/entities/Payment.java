package com.BookMyShow.demo.entities;

import com.BookMyShow.demo.enums.PaymentStatus;
import com.BookMyShow.demo.enums.PaymentType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "payment")
public class Payment {
    @Id
    private String id;
    private double amount;
    private PaymentStatus status;
    private PaymentType type;
}