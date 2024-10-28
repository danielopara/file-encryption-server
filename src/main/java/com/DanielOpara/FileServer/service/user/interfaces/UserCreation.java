package com.DanielOpara.FileServer.service.user.interfaces;

import com.DanielOpara.FileServer.dto.CreateUserDto;
import com.DanielOpara.FileServer.response.BaseResponse;

public interface UserCreation {
    BaseResponse createUser(CreateUserDto dto);
}
