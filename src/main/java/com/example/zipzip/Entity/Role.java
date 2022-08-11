package com.example.zipzip.Entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER("USER");

    String role;

    Role(String user) {
        role = user;
    }

    @Override
    public String getAuthority() {
        return role;
    }
}
