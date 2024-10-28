package com.DanielOpara.FileServer.service.user.interfaces;

import com.DanielOpara.FileServer.dto.UserDto;
import com.DanielOpara.FileServer.response.BaseResponse;

public interface UserCreation {
    BaseResponse createUser(UserDto dto);
}
