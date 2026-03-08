package com.hilip.lms.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hilip.lms.dtos.course.EnrollmentRequest;
import com.hilip.lms.services.learner.EnrollmentService;

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
}
