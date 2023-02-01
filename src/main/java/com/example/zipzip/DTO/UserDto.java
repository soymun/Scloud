package com.example.zipzip.DTO;

import com.example.zipzip.Entity.Role;
import lombok.Data;

@Data
public class UserDto {

    private Long id;

    private String email;

    private Role role;

    private Long maxSize;
}
