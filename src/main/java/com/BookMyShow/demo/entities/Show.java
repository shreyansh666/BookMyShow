package com.BookMyShow.demo.entities;
import com.BookMyShow.demo.enums.SeatStatus;
import com.BookMyShow.demo.enums.SeatType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "shows")
public class Show {
    @Id
    private String id;

    @DBRef
    private Movie movie;

    private String ScreenId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @DBRef
    private List<Seat> seats;

}


