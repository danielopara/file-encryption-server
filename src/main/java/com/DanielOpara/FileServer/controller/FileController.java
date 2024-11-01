package com.DanielOpara.FileServer.controller;

import com.DanielOpara.FileServer.response.BaseResponse;
import com.DanielOpara.FileServer.service.file.FileServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/file")
public class FileController {
    private final FileServiceImpl fileService;

    public FileController(FileServiceImpl fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    ResponseEntity<?> fileUpload(@AuthenticationPrincipal UserDetails currentUser, @RequestParam("file") MultipartFile file) throws Exception {
            String email = currentUser.getUsername();
            BaseResponse response = fileService.fileUpload(email, file);

            if(response.getStatusCode() == HttpServletResponse.SC_OK){
                return new ResponseEntity<>(response, HttpStatus.OK);
            }else{
                return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatusCode()));
            }
    }

    @PostMapping("/upload-files")
    ResponseEntity<?> uploadFiles(@AuthenticationPrincipal UserDetails currentUser,
                                  @RequestParam("files") List<MultipartFile> files){
        String email = currentUser.getUsername();
        BaseResponse response = fileService.uploadMultipleFiles(email, files);

        if(response.getStatusCode() == HttpServletResponse.SC_OK){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatusCode()));
        }
    }

    @GetMapping("/user-files")
    ResponseEntity<?> getUserFiles(@AuthenticationPrincipal UserDetails currentUser) {
        String email = currentUser.getUsername();
        BaseResponse response = fileService.getFilesByEmail(email);

        if(response.getStatusCode() == HttpServletResponse.SC_OK){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatusCode()));
        }
    }

    @GetMapping("/user-file/{id}")
    ResponseEntity<?> getUserFileByFileId(@AuthenticationPrincipal UserDetails currentUser, @PathVariable Long id) {
        String email = currentUser.getUsername();
        BaseResponse response = fileService.getAFileByEmail(id,email);

        if(response.getStatusCode() == HttpServletResponse.SC_OK){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatusCode()));
        }
    }

    @GetMapping("/download/{id}")
    ResponseEntity<?> downloadFile(@AuthenticationPrincipal UserDetails currentUser, HttpServletResponse response,
                                   @PathVariable Long id){
        String email = currentUser.getUsername();
        BaseResponse file = fileService.downloadFile(id, email, response);
        if(file.getStatusCode() == HttpServletResponse.SC_OK){
            return new ResponseEntity<>(file, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(file, HttpStatusCode.valueOf(file.getStatusCode()));
        }
    }

    @PutMapping("/update-fileName/{id}")
    ResponseEntity<?> updateFileName(@AuthenticationPrincipal UserDetails currentUser,
                                     @RequestBody FileName fileName, @PathVariable Long id ){
        try{
            String email = currentUser.getUsername();
            String response = fileService.renameFile(id, email, fileName.getFileName());
            return ResponseEntity.ok(response);
        } catch (Exception e){
            return ResponseEntity.status(500).body("error");
        }
    }

}
@Data
@AllArgsConstructor
@NoArgsConstructor
class FileName{
    private String fileName;
}
