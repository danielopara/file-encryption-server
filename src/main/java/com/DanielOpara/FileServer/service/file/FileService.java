package com.DanielOpara.FileServer.service.file;

import com.DanielOpara.FileServer.response.BaseResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    BaseResponse fileUpload(String email, MultipartFile file) throws Exception;
    BaseResponse getFilesByEmail(String email);
    BaseResponse getAFileByEmail(Long id, String email);
    BaseResponse downloadFile(Long id, String email, HttpServletResponse response);
}
