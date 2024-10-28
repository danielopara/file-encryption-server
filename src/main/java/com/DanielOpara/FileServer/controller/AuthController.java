package com.DanielOpara.FileServer.controller;

import com.DanielOpara.FileServer.dto.UserDto;
import com.DanielOpara.FileServer.response.BaseResponse;
import com.DanielOpara.FileServer.service.auth.AuthServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthServiceImpl authService;

    public AuthController(AuthServiceImpl authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody UserDto userDto){
        BaseResponse response = authService.login(userDto);

        if(response.getStatusCode() == HttpServletResponse.SC_OK){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatusCode()));
        }
    }
}
