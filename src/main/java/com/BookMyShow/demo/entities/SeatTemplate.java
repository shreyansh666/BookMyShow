package com.BookMyShow.demo.entities;


import com.BookMyShow.demo.enums.SeatStatus;
import com.BookMyShow.demo.enums.SeatType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "seat_template")
public class SeatTemplate {
    @Id
    private String id;
    private String seatNumber;
    private SeatType seatType;
    private double defaultPrice;
}
