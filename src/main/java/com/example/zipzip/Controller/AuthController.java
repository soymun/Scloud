package com.example.zipzip.Controller;


import com.example.zipzip.DTO.RegDTO;
import com.example.zipzip.Facade.AuthFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthFacade authFacade;

    public AuthController(AuthFacade authFacade) {
        this.authFacade = authFacade;
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody RegDTO regDTO){
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
