package com.hilip.lms.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hilip.lms.services.instructor.InstructorService;

@RestController
@RequestMapping("/api/instructors")
@AllArgsConstructor
public class InstructorController {
    private final InstructorService instructorService;

    @GetMapping("/{instructorId}/courses")
    public ResponseEntity<?> getInstructorCourses(
            @PathVariable("instructorId") String instructorId
    ) {
        var courses = instructorService.getInstructorCourses(instructorId);
        return ResponseEntity.ok(courses);
    }
}
