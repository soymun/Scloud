package com.example.zipzip.ExceptionHandlers;

import com.example.zipzip.Response.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<?> runtime(RuntimeException runtimeException){
        return ResponseEntity.ok(ResponseDto.builder().error(runtimeException.getMessage()).build());
    }
}
