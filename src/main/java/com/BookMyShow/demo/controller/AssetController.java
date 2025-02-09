package com.BookMyShow.demo.controller;


import com.BookMyShow.demo.dto.AssetResponse;
import com.BookMyShow.demo.entities.Asset;
import com.BookMyShow.demo.service.serviceImpl.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/asset")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @PostMapping("/register")
    public ResponseEntity<AssetResponse> registerAsset(@RequestBody Asset<?> asset) {
        AssetResponse registeredAsset = assetService.registerAsset(asset);
        return ResponseEntity.ok(registeredAsset);
    }


    @GetMapping("/data/{registerId}")
    public ResponseEntity<Asset<Map<String, Object>>> getAsset(@PathVariable String registerId) {
        System.out.println(registerId);
        Asset<Map<String, Object>> asset = assetService.getAsset(registerId, Asset.class);
        if (asset == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(asset);
    }


}



