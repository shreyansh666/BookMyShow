package com.BookMyShow.demo.entities;



import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@Document(collection = "movie")
public class Movie  {

    @Id
    private String id;
    private String title;
    private String genre;
    private int duration;
    private String language;
    private String description;
    private String director;
    private String cast;

    private LocalDateTime releaseDate;



}
