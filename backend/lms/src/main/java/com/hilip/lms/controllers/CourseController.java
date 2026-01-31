package com.hilip.lms.controllers;

import com.hilip.lms.dtos.course.CreateCourseRequest;
import com.hilip.lms.dtos.course.UpdateCourseRequest;
import com.hilip.lms.dtos.course.lessons.UpdateLessonRequest;
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
            @PathVariable("tenantId")String tenantId
    ){
        var courses = courseService.getAllCourses(tenantId);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<?> getCourseById(
            @PathVariable("courseId") String courseId
    ){
        var course = courseService.getCourseById(courseId);
        return ResponseEntity.ok(course);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<?> deleteCourse(
            @PathVariable("courseId") String courseId
    ){
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createCourse(
            @RequestPart("tenantId") String tenantId,
            @RequestPart("thumbnailFile")MultipartFile thumbnailFile,
            @RequestPart("data") CreateCourseRequest request
            ){
        courseService.createCourse(tenantId, request, thumbnailFile);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(value = "/{courseId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCourse(
            @PathVariable("courseId") String courseId,
            @RequestPart("data") UpdateCourseRequest request,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile
    ){
        courseService.updateCourse(courseId, request, thumbnailFile);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{courseId}/modules")
    public ResponseEntity<?> createModule(
            @PathVariable("courseId")String courseId
    ){
        courseService.addModuleToCourse(courseId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{courseId}/modules")
    public ResponseEntity<?> getModulesByCourseId(
            @PathVariable("courseId")String courseId
    ){
        var modules = courseService.getModulesByCourseId(courseId);
        return ResponseEntity.ok(modules);
    }


    @PostMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<?> createLesson(
            @PathVariable("moduleId")String moduleId
    ){
        courseService.addLessonToModule(moduleId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<?> getLessonsByModuleId(
            @PathVariable("moduleId")String moduleId
    ){
        var lessons = courseService.getLessonsByModuleId(moduleId);
        return ResponseEntity.ok(lessons);
    }

    @PutMapping("/modules/lessons/{lessonId}")
    public ResponseEntity<?> updateLesson(
            @PathVariable("lessonId")String lessonId,
            @RequestBody UpdateLessonRequest request
    ){
        courseService.updateLesson(lessonId, request);
        return ResponseEntity.noContent().build();
    }
}
