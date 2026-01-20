package com.hilip.lms.services;

import com.hilip.lms.models.FileResource;
import com.hilip.lms.repositories.FileResourceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class FileService {
    private final FileResourceRepository fileResourceRepository;

    public ResponseEntity<?> getFileResourceById(String fileId) throws MalformedURLException {
        FileResource fileResource = fileResourceRepository.findById(UUID.fromString(fileId))
                .orElseThrow(() -> new RuntimeException("File not found"));

        String filePath = "uploads/" + fileResource.getFileName();

        log.debug("Fetching file from path: {}", filePath);

        Resource resource = new FileSystemResource(filePath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileResource.getMimeType()))
                .body(resource);
    }
}
