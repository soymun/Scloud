package com.example.zipzip.Controller;


import com.example.zipzip.DTO.LogoutDTO;
import com.example.zipzip.DTO.RegDTO;
import com.example.zipzip.Entity.Role;
import com.example.zipzip.Entity.User;
import com.example.zipzip.Facade.AuthFacade;
import com.example.zipzip.Jwt.JwtTokenProvider;
import com.example.zipzip.Service.Impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AuthController {

    private final AuthFacade authFacade;

    public AuthController(AuthFacade authFacade) {
        this.authFacade = authFacade;
    }

    @PostMapping("/registration")
    public ResponseEntity<String> registration(@RequestBody RegDTO regDTO){
        return authFacade.registration(regDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RegDTO regDTO){
        return authFacade.login(regDTO);
    }

    @PostMapping("/registration/admin/{id}")
    public ResponseEntity<?> registrationAdmin(@PathVariable Long id){
        return authFacade.registrationAdmin(id);
    }

}
