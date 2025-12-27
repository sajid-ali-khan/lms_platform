package com.hilip.lms.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Service
public class UploadService {
    public void uploadFile(MultipartFile thumbnailFile, String newFileName) throws IOException {
        Path uploadDir = Path.of("uploads");
        Files.createDirectories(uploadDir);

        Path targetPath = uploadDir.resolve(newFileName);
        Files.copy(thumbnailFile.getInputStream(), targetPath);
    }

}
