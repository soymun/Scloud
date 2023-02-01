package com.example.zipzip.Service.Impl;

import com.example.zipzip.DTO.UserDto;
import com.example.zipzip.Entity.User;
import com.example.zipzip.Mappers.UserMappers;
import com.example.zipzip.Repo.UserRepo;
import com.example.zipzip.Service.UserServiceIntr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService implements UserServiceIntr {

    private final UserRepo userRepo;

    private final UserMappers userMappers;

    @Autowired
    public UserService(UserRepo userRepo, UserMappers userMappers) {
        this.userRepo = userRepo;
        this.userMappers = userMappers;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepo.findUserByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), user.getRole().authorities());
    }



    @Override
    public UserDto save(User userDto) {
        return userMappers.userToUserDto(userRepo.save(userDto));
    }

    @Override
    public UserDto findUserByEmail(String email){
        return userMappers.userToUserDto(userRepo.findUserByEmail(email).orElse(new User()));
    }

    @Override
    public UserDto findUserById(Long id){
        return userMappers.userToUserDto(userRepo.findUserById(id).orElseThrow(() -> new RuntimeException("User not found")));
    }

    @Override
    public User getUserToUpdate(Long id) {
        return userRepo.findUserById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
