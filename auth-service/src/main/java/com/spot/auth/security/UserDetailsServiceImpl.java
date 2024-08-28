package com.spot.auth.security;

import com.spot.auth.model.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Value("${app.user-details:user1#password1#123,user2#password2#234,user3#password3#345,system#systemPassword#000}")
    private String userDetails;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return Arrays.stream(userDetails.split(","))
                .map(this::parseUserDetails)
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public CustomUserDetails loadUserByUserId(long userId) throws UsernameNotFoundException {
        return Arrays.stream(userDetails.split(","))
                .map(this::parseUserDetails)
                .filter(u -> u.getUserId() == userId)
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("User not found with userId: " + userId));
    }

    private CustomUserDetails parseUserDetails(String userDetails) {
        String[] parts = userDetails.split("#");
        if (parts.length < 3) {
            throw new IllegalArgumentException("User details must be in the format username#password#userId. userDetails: " + userDetails);
        }
        return new CustomUserDetails(parts[0], passwordEncoder.encode(parts[1]), Collections.singletonList(new SimpleGrantedAuthority("USER")), Long.parseLong(parts[2]));
    }
}
