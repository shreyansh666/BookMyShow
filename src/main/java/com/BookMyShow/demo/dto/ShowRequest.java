package com.BookMyShow.demo.dto;

import lombok.*;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Data
public class ShowRequest {
    private String movieId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ScreenSeatTemplateConfigRequest seatConfig;
}




//screen pe ek show hai
//        scfren me show ki list hai waha abhi add ho rha hofa