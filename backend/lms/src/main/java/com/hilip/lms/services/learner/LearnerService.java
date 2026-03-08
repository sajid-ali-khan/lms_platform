package com.hilip.lms.services.learner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hilip.lms.dtos.course.CourseResponse;
import com.hilip.lms.models.Course;
import com.hilip.lms.models.enums.CourseStatus;
import com.hilip.lms.repositories.EnrollmentRepository;
import com.hilip.lms.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LearnerService {
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    public Map<String, CourseResponse> getAllEnrolledCourses(String learnerId){
        var optionalLearner = userRepository.findById(UUID.fromString(learnerId));
        if (optionalLearner.isEmpty()){
            throw new RuntimeException("Invalid learner");
        }

        var courses = enrollmentRepository.findAllEnrolledCoursesByLearnerId(UUID.fromString(learnerId));
        return courses.stream().collect(Collectors.toMap(
            course -> course.getId().toString(),    
            this::mapCourseToCourseResponse
        ));
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
