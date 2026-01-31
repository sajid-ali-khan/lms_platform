package com.hilip.lms.controllers;

import com.hilip.lms.dtos.course.CreateCourseRequest;
import com.hilip.lms.dtos.course.UpdateCourseRequest;
import com.hilip.lms.services.course.CourseService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/courses")
@AllArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/tenants/{tenantId}")
    public ResponseEntity<?> getCourses(
            @PathVariable("tenantId") String tenantId
    ) {
        var courses = courseService.getAllCourses(tenantId);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<?> getCourseById(
            @PathVariable("courseId") String courseId
    ) {
        var course = courseService.getCourseById(courseId);
        return ResponseEntity.ok(course);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<?> deleteCourse(
            @PathVariable("courseId") String courseId
    ) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createCourse(
            @RequestPart("tenantId") String tenantId,
            @RequestPart("thumbnailFile") MultipartFile thumbnailFile,
            @RequestPart("data") CreateCourseRequest request
    ) {
        courseService.createCourse(tenantId, request, thumbnailFile);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(value = "/{courseId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCourse(
            @PathVariable("courseId") String courseId,
            @RequestPart("data") UpdateCourseRequest request,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile
    ) {
        courseService.updateCourse(courseId, request, thumbnailFile);
        return ResponseEntity.noContent().build();
    }
}
