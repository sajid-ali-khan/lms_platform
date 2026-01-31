package com.hilip.lms.controllers;

import com.hilip.lms.services.course.ModuleService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/modules")
@AllArgsConstructor
public class ModuleController {
    private final ModuleService moduleService;

    @PostMapping("/courses/{courseId}")
    public ResponseEntity<?> createModule(
            @PathVariable("courseId") String courseId
    ) {
        moduleService.addModuleToCourse(courseId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<?> getModulesByCourseId(
            @PathVariable("courseId") String courseId
    ) {
        var modules = moduleService.getModulesByCourseId(courseId);
        return ResponseEntity.ok(modules);
    }
}
