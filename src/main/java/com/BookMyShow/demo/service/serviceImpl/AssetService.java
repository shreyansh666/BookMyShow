package com.BookMyShow.demo.service.serviceImpl;


import com.BookMyShow.demo.dto.AssetResponse;
import com.BookMyShow.demo.entities.Asset;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class AssetService {


    private final RedisService redisService;

    private final long ttlSeconds = 10;

    @Autowired
    public AssetService(RedisService redisService) {
        this.redisService = redisService;
    }

    public  <T> AssetResponse  registerAsset(Asset<T> asset) {
        String registerId = UUID.randomUUID().toString();
        asset.setRegisterId(registerId);
        redisService.set(registerId, asset, ttlSeconds);
        AssetResponse response = new AssetResponse(asset.getAssetType(), asset.getAssetId(), asset.getRegisterId()
        );

        return response;
    }

    public <T> Asset<T> getAsset(String registerId, Class<Asset> clazz) {
        return redisService.get(registerId, clazz);
    }
}
