package com.hilip.lms.services.course;

import com.hilip.lms.dtos.course.CourseResponse;
import com.hilip.lms.dtos.course.CreateCourseRequest;
import com.hilip.lms.dtos.course.UpdateCourseRequest;
import com.hilip.lms.exceptions.ResourceNotFoundException;
import com.hilip.lms.models.*;
import com.hilip.lms.models.enums.CourseStatus;
import com.hilip.lms.models.enums.UserRole;
import com.hilip.lms.repositories.*;
import com.hilip.lms.services.FileStorageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class CourseService {
    private final FileStorageService fileStorageService;
    private final TenantRepository tenantRepository;
    private final CourseRepository courseRepository;
    private final FileResourceRepository fileResourceRepository;
    private final UserRepository userRepository;

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
        String extension = Objects.requireNonNull(uploadedFile.getOriginalFilename()).split("\\.")[1];
        String newFileName = UUID.randomUUID() + "." + extension;
        thumbnail.setFileName(newFileName);
        thumbnail.setSize(uploadedFile.getSize());
        thumbnail.setMimeType(uploadedFile.getContentType());
        thumbnail = fileResourceRepository.save(thumbnail);
        try {
            fileStorageService.uploadFile(uploadedFile, newFileName);
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

    public Map<String, CourseResponse> getAllCourses(String tenantId) {
        var courses = courseRepository.findAllByTenantIdWithThumbnail(UUID.fromString(tenantId));

        var response = new HashMap<String, CourseResponse>();
        for (var course : courses) {
            var courseResponse = mapCourseToCourseResponse(course);
            response.put(course.getId().toString(), courseResponse);
        }
        return response;
    }

    public CourseResponse getCourseById(String courseId) {
        Course course = courseRepository.findById(UUID.fromString(courseId))
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        return mapCourseToCourseResponse(course);
    }

    public void updateCourse(String courseId, UpdateCourseRequest request, MultipartFile thumbnailFile) {
        Course course = courseRepository.findById(UUID.fromString(courseId))
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (request.title() != null && !request.title().isBlank()) {
            course.setTitle(request.title());
        }

        if (request.description() != null) {
            course.setDescription(request.description());
        }

        if (request.status() != null) {
            course.setStatus(request.status());
        }

        if (request.instructorId() != null && !request.instructorId().isBlank()) {
            User instructor = userRepository.findById(UUID.fromString(request.instructorId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));

            if (!instructor.getRole().equals(UserRole.INSTRUCTOR)) {
                throw new IllegalArgumentException(instructor.getFullName() + " is not an instructor");
            }

            if (!instructor.getTenant().getId().equals(course.getTenant().getId())) {
                throw new IllegalArgumentException("Instructor does not belong to the same tenant as the course");
            }

            course.setInstructor(instructor);
        }

        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            FileResource oldThumbnail = course.getThumbnailFile();

            FileResource newThumbnail = new FileResource();
            String extension = Objects.requireNonNull(thumbnailFile.getOriginalFilename()).split("\\.")[1];
            String newFileName = UUID.randomUUID() + "." + extension;
            newThumbnail.setFileName(newFileName);
            newThumbnail.setSize(thumbnailFile.getSize());
            newThumbnail.setMimeType(thumbnailFile.getContentType());
            newThumbnail = fileResourceRepository.save(newThumbnail);

            try {
                fileStorageService.uploadFile(thumbnailFile, newFileName);
                course.setThumbnailFile(newThumbnail);

                if (oldThumbnail != null) {
                    fileStorageService.deleteFile(oldThumbnail.getFileName());
                    fileResourceRepository.delete(oldThumbnail);
                }
            } catch (Exception e) {
                log.error("Error uploading file", e);
                fileResourceRepository.delete(newThumbnail);
                throw new RuntimeException("Error uploading file");
            }
        }

        courseRepository.save(course);
        log.info("Course updated successfully: {}", courseId);
    }

    public void deleteCourse(String courseId) {
        Course course = courseRepository.findById(UUID.fromString(courseId))
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        FileResource thumbnail = course.getThumbnailFile();

        courseRepository.delete(course);
        log.debug("Course deleted successfully: {}", courseId);

        if (thumbnail != null) {
            try {
                fileStorageService.deleteFile(thumbnail.getFileName());
                fileResourceRepository.delete(thumbnail);
                log.debug("Thumbnail file deleted successfully: {}", thumbnail.getFileName());
            } catch (Exception e) {
                log.error("Error deleting thumbnail file: {}", thumbnail.getFileName(), e);
            }
        }
    }

    private CourseResponse mapCourseToCourseResponse(Course course) {
        Map<CourseStatus, Boolean> statusOptions = new HashMap<>();
        for (CourseStatus status : CourseStatus.values()) {
            statusOptions.put(status, status.equals(course.getStatus()));
        }

        return new CourseResponse(
                course.getId().toString(),
                course.getTitle(),
                course.getDescription(),
                course.getThumbnailFile() != null ? course.getThumbnailFile().getId().toString() : null,
                course.getStatus(),
                statusOptions
        );
    }
}
