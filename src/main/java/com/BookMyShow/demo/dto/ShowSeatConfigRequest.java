package com.BookMyShow.demo.dto;
import lombok.*;




@Data
@Builder
public class ShowSeatConfigRequest {
    private int regularCount;
    private double regularPrice;

    private int premiumCount;
    private double premiumPrice;

    private int vipCount;
    private double vipPrice;
}
