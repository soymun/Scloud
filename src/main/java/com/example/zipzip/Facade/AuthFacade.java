package com.example.zipzip.Facade;


import com.example.zipzip.DTO.RegDTO;
import com.example.zipzip.DTO.UserDto;
import com.example.zipzip.Entity.Role;
import com.example.zipzip.Entity.User;
import com.example.zipzip.Jwt.JwtTokenProvider;
import com.example.zipzip.Service.Impl.FileService;
import com.example.zipzip.Service.Impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AuthFacade {
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final FileService fileService;

    @Autowired
    public AuthFacade(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, FileService fileService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.fileService = fileService;
    }


    public ResponseEntity<String> registration(RegDTO regDTO){
        UserDto user1 = userService.findUserByEmail(regDTO.getEmail());
        if(user1.getId() != null){
            throw new RuntimeException("Этот емайл уже зарегестрирован");
        }
        User user = new User();
        user.setEmail(regDTO.getEmail());
        user.setPassword(passwordEncoder.encode(regDTO.getPassword()));
        user.setRole(Role.USER);
        user.setMaxSize(8L);
        fileService.createDirById(userService.save(user).getId().toString());
        return ResponseEntity.ok("Успешно");
    }

    public ResponseEntity<?> login(RegDTO regDTO){
        try {
            UserDto user = userService.findUserByEmail(regDTO.getEmail());
            if(user == null){
                throw new RuntimeException("Такого пользователя не существует");
            }
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(regDTO.getEmail(), regDTO.getPassword()));
            String token = jwtTokenProvider.createToken(user.getEmail(), user.getRole());
            Map<String, String> response = new HashMap<>();
            response.put("email", user.getEmail());
            response.put("id", user.getId().toString());
            response.put("token", token);
            return ResponseEntity.ok(response);
        }
        catch (AuthenticationException e){
            throw new RuntimeException("Регистрация провалилась");
        }
    }

    public ResponseEntity<?> registrationAdmin(Long id){
        User userDto = userService.getUserToUpdate(id);
        userDto.setRole(Role.ADMIN);
        userService.save(userDto);
        return ResponseEntity.ok("Админ сохранён");
    }
}
