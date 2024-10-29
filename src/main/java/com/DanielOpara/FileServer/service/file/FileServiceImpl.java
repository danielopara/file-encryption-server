package com.DanielOpara.FileServer.service.file;

import com.DanielOpara.FileServer.model.FileModel;
import com.DanielOpara.FileServer.repository.FileRepository;
import com.DanielOpara.FileServer.response.BaseResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;

    @Value("${ENC_KEY}")
    private String ENC_KEY;
    private static final String ALGORITHM = "AES";

    public FileServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    private byte[] encryptFileToByteArray(InputStream inputStream) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(ENC_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] inputBytes = new byte[64];
            int bytesRead;

            while ((bytesRead = inputStream.read(inputBytes)) != -1) {
                byte[] outputBytes = cipher.update(inputBytes, 0, bytesRead);
                if (outputBytes != null) {
                    byteArrayOutputStream.write(outputBytes);
                }
            }

            byte[] outputBytes = cipher.doFinal();
            if (outputBytes != null) {
                byteArrayOutputStream.write(outputBytes);
            }

            return byteArrayOutputStream.toByteArray();
        }
    }

    @Override
    public BaseResponse fileUpload(String email, MultipartFile file) throws Exception {
        try{
            byte[] encryptedData = encryptFileToByteArray(file.getInputStream());
            FileModel encryptedFile = new FileModel();
            encryptedFile.setFileName(file.getOriginalFilename() + ".enc");
            encryptedFile.setFileData(encryptedData);
            encryptedFile.setEmail(email);
            fileRepository.save(encryptedFile);

            return new BaseResponse(
                    HttpServletResponse.SC_OK,
                    "file saved",
                    file.getOriginalFilename()
            );

        }catch (Exception e){
            return new BaseResponse(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal Server Error",
                    e.getMessage()
            );
        }

    }
}