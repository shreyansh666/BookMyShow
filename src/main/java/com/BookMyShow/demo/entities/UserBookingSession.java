package com.BookMyShow.demo.entities;


import com.BookMyShow.demo.enums.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;



import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@Builder
@Document(collection = "user_booking_session")
public class UserBookingSession {

    @Id
    private String sessionId;

    @DBRef(lazy = true)
    private User user;

    @DBRef(lazy = true)
    private Show show;

    @DBRef(lazy = true)
    private List<ShowSeat> seats;


    private SessionStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}

