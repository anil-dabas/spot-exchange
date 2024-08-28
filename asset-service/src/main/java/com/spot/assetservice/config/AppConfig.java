package com.spot.assetservice.config;

import com.spot.auth.config.SecurityConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "com.spot.auth.security") // Adjust the package if JwtTokenProvider is located elsewhere
@Import(SecurityConfig.class)
public class AppConfig {
    // This class can remain empty. It's just to hold the annotation
}
