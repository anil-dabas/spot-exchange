package com.spot.notification.controller;



import com.spot.auth.model.CustomUserDetails;
import com.spot.auth.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HomeController {

    @Value("${spring.application.name}")
    private String serviceName;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    @GetMapping("/")
    public String home() {
        return serviceName;
    }

    @GetMapping("/testN")
    public String test(@RequestHeader(value = "Authorization", required = false) String headerValue) {
        String token = headerValue.split(" ")[1];
        log.debug(token);
        String user = jwtTokenProvider.getUsernameFromToken(token);
        return "Test Notification Success User : " + user;
    }

    @GetMapping("/user")
    public String userDetails(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return "UserDetails Notification Success User : " + userDetails.getUsername()+
                "\t UserId : " + userDetails.getUserId();
    }


}
