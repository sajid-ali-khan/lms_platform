package com.hilip.lms.controllers;

import com.hilip.lms.dtos.course.CreateCourseRequest;
import com.hilip.lms.services.CourseService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/tenants/{tenantId}/courses")
@AllArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createCourse(
            @PathVariable("tenantId")String tenantId,
            @RequestPart("thumbnailFile")MultipartFile thumbnailFile,
            @RequestPart("data") CreateCourseRequest request
            ){
        courseService.createCourse(tenantId, request, thumbnailFile);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{courseId}/modules")
    public ResponseEntity<?> createModule(
            @PathVariable("tenantId")String tenantId,
            @PathVariable("courseId")String courseId
    ){
        courseService.addModuleToCourse(tenantId, courseId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
