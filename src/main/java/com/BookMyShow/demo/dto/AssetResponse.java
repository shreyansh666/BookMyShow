package com.BookMyShow.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetResponse {
    private String assetType;
    private String assetId;
    private String registerId;
}
