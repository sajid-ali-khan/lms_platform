package com.hilip.lms.course.enrollment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hilip.lms.course.enrollment.dto.EnrollmentRequest;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/enrollments")
@AllArgsConstructor
public class EnrollmentsController {
    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<?> addEnrollment(@RequestBody EnrollmentRequest request){
        enrollmentService.addEnrollment(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<?> getEnrollmentsByCourse(@PathVariable("courseId") String courseId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourse(courseId));
    }
}
