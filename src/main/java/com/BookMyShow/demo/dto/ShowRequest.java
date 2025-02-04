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
    private ShowSeatConfigRequest seatConfig;
}
