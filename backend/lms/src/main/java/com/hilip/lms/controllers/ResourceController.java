package com.hilip.lms.controllers;

import com.hilip.lms.services.FileService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;

@RestController
@RequestMapping("/api/resources")
@AllArgsConstructor
public class ResourceController {
    private final FileService fileService;

    @GetMapping("/{resourceId}")
    public ResponseEntity<?> getResourceById(
            @PathVariable("resourceId")String resourceId
    ) throws MalformedURLException {
        return fileService.getFileResourceById(resourceId);
    }
}
