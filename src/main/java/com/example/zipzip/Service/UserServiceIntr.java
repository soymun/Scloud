package com.example.zipzip.Service;

import com.example.zipzip.DTO.UserDto;
import com.example.zipzip.Entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserServiceIntr extends UserDetailsService {

    UserDto save(User userDto);

    UserDto findUserByEmail(String email);

    UserDto findUserById(Long id);

    UserDto patchUser(User user);

    void deleteUserById(Long id);
}
