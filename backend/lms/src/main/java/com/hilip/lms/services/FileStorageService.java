package com.hilip.lms.services;

import com.hilip.lms.models.FileResource;
import com.hilip.lms.repositories.FileResourceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Service for managing file storage operations including upload, deletion, and retrieval.
 * Handles both physical file storage on disk and file metadata in the database.
 */
@Service
@AllArgsConstructor
@Slf4j
public class FileStorageService {
    private static final String UPLOAD_DIR = "uploads";
    private final FileResourceRepository fileResourceRepository;

    /**
     * Uploads a file to the storage directory.
     *
     * @param file        The multipart file to upload
     * @param newFileName The name to save the file with
     * @throws IOException If file upload fails
     */
    public void uploadFile(MultipartFile file, String newFileName) throws IOException {
        Path uploadDir = Path.of(UPLOAD_DIR);
        Files.createDirectories(uploadDir);

        Path targetPath = uploadDir.resolve(newFileName);
        Files.copy(file.getInputStream(), targetPath);

        log.debug("File uploaded successfully: {}", newFileName);
    }

    /**
     * Deletes a file from the storage directory.
     *
     * @param fileName The name of the file to delete
     * @throws IOException If file deletion fails
     */
    public void deleteFile(String fileName) throws IOException {
        Path uploadDir = Path.of(UPLOAD_DIR);
        Path filePath = uploadDir.resolve(fileName);
        boolean deleted = Files.deleteIfExists(filePath);

        if (deleted) {
            log.debug("File deleted successfully: {}", fileName);
        } else {
            log.warn("File not found for deletion: {}", fileName);
        }
    }

    /**
     * Retrieves a file resource by its ID and returns it as an HTTP response.
     *
     * @param fileId The UUID of the file resource
     * @return ResponseEntity containing the file resource
     */
    public ResponseEntity<?> getFileResourceById(String fileId) {
        FileResource fileResource = fileResourceRepository.findById(UUID.fromString(fileId))
                .orElseThrow(() -> new RuntimeException("File not found"));

        String filePath = UPLOAD_DIR + "/" + fileResource.getFileName();

        log.debug("Fetching file from path: {}", filePath);

        Resource resource = new FileSystemResource(filePath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileResource.getMimeType()))
                .body(resource);
    }
}
