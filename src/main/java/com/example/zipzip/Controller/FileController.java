package com.example.zipzip.Controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileController {

    @GetMapping("/get")
    public String get(){
        return "get";
    }
}
