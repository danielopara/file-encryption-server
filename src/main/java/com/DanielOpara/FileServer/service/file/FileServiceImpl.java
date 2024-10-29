package com.DanielOpara.FileServer.service.file;

import com.DanielOpara.FileServer.model.FileModel;
import com.DanielOpara.FileServer.repository.FileRepository;
import com.DanielOpara.FileServer.response.BaseResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

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

    private byte[] decryptFile(byte[] encryptedData) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(ENC_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] outputBytes = cipher.doFinal(encryptedData);
            byteArrayOutputStream.write(outputBytes);
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

    @Override
    public BaseResponse getFilesByEmail(String email) {
       try{
           Optional<FileModel> userEmail = fileRepository.findByEmail(email);
           if(userEmail.isEmpty()){
               return new BaseResponse(
                       HttpServletResponse.SC_BAD_REQUEST,
                       "non-existence",
                       null
               );
           }

           List<Map<String, Object>> fileNames = userEmail.stream()
                   .map(file->{
                       Map<String, Object> fileInfo = new HashMap<>();
                       fileInfo.put("id", file.getId());
                       fileInfo.put("fileName", file.getFileName());
                       return fileInfo;
                   })
                   .collect(Collectors.toList());

           return new BaseResponse(
                   HttpServletResponse.SC_OK,
                   "Filenames retrieved successfully.",
                   fileNames
           );

       }catch (Exception e) {
           return new BaseResponse(
                   HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                   "Internal Server Error",
                   e.getMessage()
           );
       }
    }

    @Override
    public BaseResponse getAFileByEmail(Long id, String email) {
        try {
            Optional<FileModel> fileOptional = fileRepository.findById(id);

            if (fileOptional.isEmpty()) {
                return new BaseResponse(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "File not found.",
                        null
                );
            }

            FileModel file = fileOptional.get();

            if (!Objects.equals(file.getEmail(), email)) {
                return new BaseResponse(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Access denied: Email does not match file owner.",
                        null
                );
            }

            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("id", file.getId());
            fileInfo.put("fileName", file.getFileName());

            return new BaseResponse(
                    HttpServletResponse.SC_OK,
                    "File retrieved successfully.",
                    fileInfo
            );

        } catch (Exception e) {
            return new BaseResponse(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal Server Error",
                    e.getMessage()
            );
        }
    }

    @Override
    public BaseResponse downloadFile(Long id, String email, HttpServletResponse response) {

        try{

            FileModel fileModel = fileRepository.findById(id)
                    .orElseThrow(() -> new Exception("File not found"));

            if(!Objects.equals(fileModel.getEmail(), email)){
                return new BaseResponse(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "cannot download this file",
                        null
                );
            }

            // Decrypt the file data
            byte[] decryptedData = decryptFile(fileModel.getFileData());

            // Set response headers
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileModel.getFileName() + "\"");
            response.setContentLength(decryptedData.length);

            // Write decrypted data to response output stream
            response.getOutputStream().write(decryptedData);
            response.getOutputStream().flush();

            return new BaseResponse(
                    HttpServletResponse.SC_OK,
                    "file downloaded",
                    fileModel.getFileName()
            );
        }catch(Exception e){
            return new BaseResponse(
              HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "Internal Server Error",
              e.getMessage()
            );
        }
    }

}
