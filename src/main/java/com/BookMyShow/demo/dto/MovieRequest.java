package com.BookMyShow.demo.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class MovieRequest {
    private String venueId;
    private String title;
    private String genre;
    private int duration;
    private String description;
    private LocalDateTime eventDate;
}
