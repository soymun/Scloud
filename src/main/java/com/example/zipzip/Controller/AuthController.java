package com.example.zipzip.Controller;


import com.example.zipzip.DTO.LogoutDTO;
import com.example.zipzip.DTO.RegDTO;
import com.example.zipzip.Entity.Role;
import com.example.zipzip.Entity.User;
import com.example.zipzip.Jwt.JwtTokenProvider;
import com.example.zipzip.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AuthController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/registration")
    public ResponseEntity<String> registration(@RequestBody RegDTO regDTO){
        User user = userService.findUserByEmail(regDTO.getEmail());
        if(user != null){
            throw new RuntimeException("User found");
        }
        user = new User();
        user.setEmail(regDTO.getEmail());
        user.setPassword(passwordEncoder.encode(regDTO.getPassword()));
        user.setRole(List.of(Role.USER));
        userService.save(user);
        return ResponseEntity.ok("Suggest");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RegDTO regDTO){
        try {
            User user = userService.findUserByEmail(regDTO.getEmail());
            if(user == null){
                throw new RuntimeException("User not found");
            }
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(regDTO.getEmail(), regDTO.getPassword()));
            String token = jwtTokenProvider.createToken(user.getEmail(), user.getRole().get(0));
            Map<String, String> response = new HashMap<>();
            response.put("email", user.getEmail());
            response.put("id", user.getId().toString());
            response.put("token", token);
            return ResponseEntity.ok(response);
        }
        catch (AuthenticationException e){
            throw new RuntimeException("user don't authentication");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest servletRequest, HttpServletResponse response, @RequestBody LogoutDTO logoutDTO){
        User user = userService.findUserByEmail(logoutDTO.getEmail());
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(servletRequest, response, new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        return ResponseEntity.ok("Suggest");
    }
}
