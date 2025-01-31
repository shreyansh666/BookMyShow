package com.BookMyShow.demo.entities;
import com.BookMyShow.demo.enums.BookingStatus;
import com.BookMyShow.demo.enums.PaymentType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "booking")
public class Booking {
    @Id
    private String id;

    @DBRef
    private User user;


    private String showId;

    private List<String> seatIds;


    private PaymentType payment;

    private BookingStatus status;
}