package com.example.zipzip.Entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public enum Role {
    USER(Set.of(Permitions.USER)), ADMIN(Set.of(Permitions.USER, Permitions.ADMIN));

    final Set<Permitions> sp;

    Role(Set<Permitions> sp) {
        this.sp = sp;
    }

    public List<SimpleGrantedAuthority> authorities(){
        return sp.stream().map(Permitions::getP).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}
