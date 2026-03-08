package com.hilip.lms.services.learner;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hilip.lms.dtos.course.EnrollmentRequest;
import com.hilip.lms.models.Enrollment;
import com.hilip.lms.repositories.CourseRepository;
import com.hilip.lms.repositories.EnrollmentRepository;
import com.hilip.lms.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public void addEnrollment(EnrollmentRequest request) {
        var optionalCourse = courseRepository.findById(UUID.fromString(request.courseId()));
        var optionalLearner = userRepository.findById(UUID.fromString(request.learnerId()));

        if (optionalCourse.isEmpty() || optionalLearner.isEmpty()){
            throw new RuntimeException("Invalid course or users.");
        }

        var newEnrollment = new Enrollment();
        newEnrollment.setCourse(optionalCourse.get());
        newEnrollment.setLearner(optionalLearner.get());

        enrollmentRepository.save(newEnrollment);
    }
}
