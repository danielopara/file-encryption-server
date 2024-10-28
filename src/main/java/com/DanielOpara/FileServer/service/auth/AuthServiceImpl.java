package com.DanielOpara.FileServer.service.auth;

import com.DanielOpara.FileServer.dto.UserDto;
import com.DanielOpara.FileServer.jwt.JwtService;
import com.DanielOpara.FileServer.response.BaseResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;



    @Override
    public BaseResponse login(UserDto user) {
        try{
            if(user == null || user.getEmail().isEmpty() || user.getPassword().isEmpty()){
                return new BaseResponse(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "provided the parameters",
                        null
                );
            }

            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    user.getPassword()
            ));

            if(authenticate == null || !authenticate.isAuthenticated()){
                return new BaseResponse(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "username or password is incorrect",
                        null
                );
            }

            String token = jwtService.generateAccessToken(authenticate);

            return new BaseResponse(
                    HttpServletResponse.SC_OK,
                    "logged in successfully",
                    token
            );

        }catch (Exception e) {
            return new BaseResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal Server Error",
                    e.getMessage());
        }
    }
}
