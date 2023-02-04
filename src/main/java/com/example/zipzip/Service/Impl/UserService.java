package com.example.zipzip.Service.Impl;

import com.example.zipzip.DTO.UserDto;
import com.example.zipzip.Entity.User;
import com.example.zipzip.Mappers.UserMappers;
import com.example.zipzip.Repo.UserRepo;
import com.example.zipzip.Service.UserServiceIntr;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
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
        log.info("Пользователь с id {} зашёл", user.getId());
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), user.getRole().authorities());
    }



    @Override
    public UserDto save(User userDto) {
        log.info("Пользователь с email {} зарегистрировался", userDto.getEmail());
        return userMappers.userToUserDto(userRepo.save(userDto));
    }

    @Override
    public UserDto findUserByEmail(String email){
        log.info("Поиск пользователя с email {}", email);
        return userMappers.userToUserDto(userRepo.findUserByEmail(email).orElse(new User()));
    }

    @Override
    public UserDto findUserById(Long id){
        log.info("Поиск пользователя с id {}", id);
        return userMappers.userToUserDto(userRepo.findUserById(id).orElseThrow(() -> new RuntimeException("User not found")));
    }

    @Override
    public UserDto patchUser(User user){
        User user1 = userRepo.findUserById(user.getId()).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        log.info("Изменение пользователя с id {}", user1.getId());
        if(user.getEmail() != null){
            user1.setEmail(user.getEmail());
        }
        if(user.getPassword() != null){
            user1.setPassword(user.getPassword());
        }
        if(user.getRole() != null){
            user1.setRole(user.getRole());
        }
        if(user.getMaxSize() != null){
            user1.setMaxSize(user.getMaxSize()+(user1.getMaxSize()-user1.getFreeSize()));
        }
        if(user.getFreeSize() != null){
            user1.setFreeSize(user.getFreeSize());
        }
        return userMappers.userToUserDto(userRepo.save(user1));
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        log.info("Удаление пользователя с id {}", id);
        userRepo.deleteById(id);
    }
}
