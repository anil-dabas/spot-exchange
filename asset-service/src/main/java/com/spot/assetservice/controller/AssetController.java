package com.spot.assetservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class AssetController {

    @Value("${spring.application.name}")
    private String serviceName;

    @GetMapping("/")
    public String home() {
        return serviceName;
    }

    @GetMapping("/test/asset-service")
    public String test() {
        return "Test Asset Success";
    }
}
