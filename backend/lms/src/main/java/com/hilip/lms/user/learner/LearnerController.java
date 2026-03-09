package com.hilip.lms.user.learner;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/learners")
@AllArgsConstructor
public class LearnerController {
    private final LearnerService learnerService;

    @GetMapping("/{learnerId}/courses")
    public ResponseEntity<?> getEnrolledCourses(@PathVariable("learnerId") String learnerId) {
        var courses = learnerService.getAllEnrolledCourses(learnerId);
        return ResponseEntity.ok(courses);
    }
}
