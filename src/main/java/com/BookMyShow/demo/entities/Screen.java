package com.BookMyShow.demo.entities;

import com.BookMyShow.demo.enums.ScreenType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "screen")
public class Screen {

    @Id
    private String id;
    private String name;
    private ScreenType type;



    @DBRef
    private List<SeatTemplate> seats;



}

