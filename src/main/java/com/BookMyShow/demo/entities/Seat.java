package com.BookMyShow.demo.entities;
import com.BookMyShow.demo.enums.SeatStatus;
import com.BookMyShow.demo.enums.SeatType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "seats")
public class Seat {
    @Id
    private String id;
    private String seatNumber;
    private SeatStatus status;
    private SeatType seatType;
    private double price;
    private String ShowId;

    @Version
    private Integer version;


}