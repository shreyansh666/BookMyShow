package com.BookMyShow.demo.entities;

import com.BookMyShow.demo.enums.SeatStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "show_seat")
public class ShowSeat {
    @Id
    private String id;

    @DBRef
    private SeatTemplate seatTemplate;

    private double price;
    private SeatStatus status;
    private String showId;
}

