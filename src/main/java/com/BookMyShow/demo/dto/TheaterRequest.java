package com.BookMyShow.demo.dto;



import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Data
public class TheaterRequest {
    private String name;
    private String city;
    private String state;
    private String pinCode;
    private String locality;
}
