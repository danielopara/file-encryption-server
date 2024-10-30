package com.DanielOpara.FileServer.service.file;

import com.DanielOpara.FileServer.response.BaseResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    BaseResponse fileUpload(String email, MultipartFile file) throws Exception;
    BaseResponse uploadMultipleFiles(String email, List<MultipartFile> files);
    BaseResponse getFilesByEmail(String email);
    BaseResponse getAFileByEmail(Long id, String email);
    BaseResponse downloadFile(Long id, String email, HttpServletResponse response);
    String renameFile(Long id, String email, String fileName);
}
