package com.BookMyShow.demo.entities;

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
    private String theaterId;

    @DBRef
    private List<Show> shows;
}

