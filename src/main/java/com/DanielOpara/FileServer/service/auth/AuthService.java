package com.DanielOpara.FileServer.service.auth;

import com.DanielOpara.FileServer.dto.UserDto;
import com.DanielOpara.FileServer.response.BaseResponse;

public interface AuthService {
    BaseResponse login(UserDto user);
}
