package com.BookMyShow.demo.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScreenSeatTemplateConfigRequest {

    private int regularCount;
    private double regularPrice;

    private int premiumCount;
    private double premiumPrice;

    private int vipCount;
    private double vipPrice;

}
