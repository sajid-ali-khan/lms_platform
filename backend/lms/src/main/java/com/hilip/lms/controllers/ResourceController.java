package com.hilip.lms.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hilip.lms.services.shared.FileStorageService;


@RestController
@RequestMapping("/api/resources")
@AllArgsConstructor
@Slf4j
public class ResourceController {
    private final FileStorageService fileStorageService;

    @GetMapping("/{resourceId}")
    public ResponseEntity<?> getResourceById(
            @PathVariable("resourceId")String resourceId
    ) {
        log.debug("Received request to fetch resource with ID: {}", resourceId);
        return fileStorageService.getFileResourceById(resourceId);
    }
}
