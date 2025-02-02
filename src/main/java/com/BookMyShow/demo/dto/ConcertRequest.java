package com.BookMyShow.demo.dto;
import lombok.*;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Data
public class ConcertRequest {
    private String venueId;
    private String artistName;
    private String description;
    private LocalDateTime eventDate;
}
