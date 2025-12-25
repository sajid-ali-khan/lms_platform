package com.hilip.lms.services;

import com.hilip.lms.dtos.course.CreateCourseRequest;
import com.hilip.lms.dtos.course.lessons.UpdateLessonRequest;
import com.hilip.lms.exceptions.ResourceNotFoundException;
import com.hilip.lms.models.*;
import com.hilip.lms.models.Module;
import com.hilip.lms.models.enums.UserRole;
import com.hilip.lms.repositories.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class CourseService {
    private final UploadService uploadService;
    private final TenantRepository tenantRepository;
    private final CourseRepository courseRepository;
    private final FileResourceRepository fileResourceRepository;
    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;

    public void createCourse(String tenantId, CreateCourseRequest request, MultipartFile uploadedFile) {
        Tenant tenant = tenantRepository.findById(UUID.fromString(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
        User instructor = userRepository.findById(UUID.fromString(request.instructorId()))
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));

        if (!instructor.getRole().equals(UserRole.INSTRUCTOR)){
            throw new IllegalArgumentException(instructor.getFullName() + " is not an instructor");
        }

        if (!instructor.getTenant().getId().equals(tenant.getId())){
            throw new IllegalArgumentException("Instructor does not belong to the tenant");
        }

        FileResource thumbnail = new FileResource();
        String newFileName = UUID.randomUUID().toString();
        thumbnail.setFileName(newFileName);
        thumbnail.setSize(uploadedFile.getSize());
        thumbnail.setMimeType(uploadedFile.getContentType());
        thumbnail = fileResourceRepository.save(thumbnail);
        try {
            uploadService.uploadFile(uploadedFile, newFileName);
        } catch (Exception e) {
            log.error("Error uploading file", e);
            throw new RuntimeException("Error uploading file");
        }
        Course newCourse = new Course();
        newCourse.setTitle(request.title());
        newCourse.setDescription(request.description());
        newCourse.setInstructor(instructor);
        newCourse.setThumbnailFile(thumbnail);
        newCourse.setTenant(tenant);
        newCourse.setStatus(request.visibility());

        courseRepository.save(newCourse);
    }

    public void addModuleToCourse(String courseId) {
        Course course = courseRepository.findById(UUID.fromString(courseId))
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        var module = new Module();
        int sequenceOrder = course.getModules().size() + 1;
        module.setTitle("Module " + sequenceOrder);
        module.setSequenceOrder(sequenceOrder);
        module.setIsPublished(true);
        module.setCourse(course);

        course.getModules().add(module);
        courseRepository.save(course);
    }

    public void addLessonToModule(String moduleId) {
        Module module = moduleRepository.findById(UUID.fromString(moduleId))
                .orElseThrow(() -> new ResourceNotFoundException("Module not found"));

        var lesson = new com.hilip.lms.models.Lesson();
        int sequenceOrder = module.getLessons().size() + 1;
        lesson.setTitle("Lesson " + sequenceOrder);
        lesson.setSequenceOrder(sequenceOrder);
        lesson.setIsPublished(true);
        lesson.setModule(module);
        module.getLessons().add(lesson);
        moduleRepository.save(module);
    }

    public void updateLesson(String lessonId, UpdateLessonRequest request) {
        Lesson lesson = lessonRepository.findById(UUID.fromString(lessonId))
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

        lesson.setTitle(request.title());
        lesson.setContent(request.content());
        lesson.setType(request.type());
        if (request.resourceUrl() != null) lesson.setResourceUrl(request.resourceUrl());
        if (request.isPublished() != null) lesson.setIsPublished(request.isPublished());
        lessonRepository.save(lesson);
    }
}
