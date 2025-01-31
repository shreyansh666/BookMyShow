package com.BookMyShow.demo.dto;

import lombok.*;

@Data
@Builder
public class VenueRequest {
    private String name;
    private String address;
    private int capacity;
}