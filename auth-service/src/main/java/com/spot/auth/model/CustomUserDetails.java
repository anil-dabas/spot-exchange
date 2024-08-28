package com.spot.auth.model;


import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
public class CustomUserDetails extends User {

    private final long userId;

    public CustomUserDetails(String username, String password, Collection<GrantedAuthority> authorities,long userId) {
        super(username, password, authorities);
        this.userId = userId;
    }


}
