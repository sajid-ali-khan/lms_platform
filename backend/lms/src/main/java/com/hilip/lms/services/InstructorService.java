package com.hilip.lms.services;

import com.hilip.lms.dtos.course.CourseResponse;
import com.hilip.lms.exceptions.ResourceNotFoundException;
import com.hilip.lms.models.User;
import com.hilip.lms.models.enums.UserRole;
import com.hilip.lms.repositories.UserRepository;
import com.hilip.lms.services.course.CourseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class InstructorService {
    private final UserRepository userRepository;
    private final CourseService courseService;

    public Map<String, CourseResponse> getInstructorCourses(String instructorId) {
        log.debug("Fetching courses for instructor: {}", instructorId);

        User instructor = userRepository.findById(UUID.fromString(instructorId))
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));

        if (!instructor.getRole().equals(UserRole.INSTRUCTOR)) {
            throw new IllegalArgumentException("User is not an instructor");
        }

        var courses = instructor.getInstructedCourses();
        var response = new HashMap<String, CourseResponse>();

        for (var course : courses) {
            var courseResponse = courseService.getCourseById(course.getId().toString());
            response.put(course.getId().toString(), courseResponse);
        }

        log.debug("Found {} courses for instructor: {}", response.size(), instructorId);
        return response;
    }
}
