package com.DanielOpara.FileServer.service.user.implementation;

import com.DanielOpara.FileServer.dto.UserDto;
import com.DanielOpara.FileServer.model.User;
import com.DanielOpara.FileServer.repository.UserRepository;
import com.DanielOpara.FileServer.response.BaseResponse;
import com.DanielOpara.FileServer.service.user.interfaces.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public BaseResponse createUser(UserDto dto) {
        try{
            if (dto.getEmail() == null || dto.getEmail().isEmpty() ||
                    dto.getPassword() == null || dto.getPassword().isEmpty()) {
                return new BaseResponse(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Email and password cannot be empty.",
                        null
                );
            }

            if(!isEmailValid(dto.getEmail())){
                return new BaseResponse(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "enter a valid email",
                        null
                );
            }

            User user = new User();
            String encodedPassword = passwordEncoder.encode(dto.getPassword());

            user.setEmail(dto.getEmail());
            user.setPassword(encodedPassword);

            userRepository.save(user);

            return new BaseResponse(
                    HttpServletResponse.SC_OK,
                    "user created",
                    user.getEmail()
            );

        }catch (Exception e){
            return new BaseResponse(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal Server Error",
                    e.getMessage()
            );
        }
    }

    private boolean isEmailValid(String email){
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }
}
