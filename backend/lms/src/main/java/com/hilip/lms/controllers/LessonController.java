package com.hilip.lms.controllers;

import com.hilip.lms.dtos.course.lessons.UpdateLessonRequest;
import com.hilip.lms.services.course.LessonService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lessons")
@AllArgsConstructor
public class LessonController {
    private final LessonService lessonService;

    @PostMapping("/modules/{moduleId}")
    public ResponseEntity<?> createLesson(
            @PathVariable("moduleId") String moduleId
    ) {
        lessonService.addLessonToModule(moduleId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/modules/{moduleId}")
    public ResponseEntity<?> getLessonsByModuleId(
            @PathVariable("moduleId") String moduleId
    ) {
        var lessons = lessonService.getLessonsByModuleId(moduleId);
        return ResponseEntity.ok(lessons);
    }

    @PutMapping("/{lessonId}")
    public ResponseEntity<?> updateLesson(
            @PathVariable("lessonId") String lessonId,
            @RequestBody UpdateLessonRequest request
    ) {
        lessonService.updateLesson(lessonId, request);
        return ResponseEntity.noContent().build();
    }
}
