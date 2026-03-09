package com.hilip.lms.course.enrollment;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hilip.lms.course.CourseRepository;
import com.hilip.lms.course.enrollment.dto.EnrollmentRequest;
import com.hilip.lms.user.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public void addEnrollment(EnrollmentRequest request) {
        UUID courseId = UUID.fromString(request.courseId());
        UUID learnerId = UUID.fromString(request.learnerId());

        var optionalCourse = courseRepository.findById(courseId);
        var optionalLearner = userRepository.findById(learnerId);

        if (optionalCourse.isEmpty() || optionalLearner.isEmpty()){
            throw new RuntimeException("Invalid course or user.");
        }

        if (enrollmentRepository.existsByLearnerIdAndCourseId(learnerId, courseId)) {
            throw new RuntimeException("Already enrolled in this course.");
        }

        var newEnrollment = new Enrollment();
        newEnrollment.setCourse(optionalCourse.get());
        newEnrollment.setLearner(optionalLearner.get());

        enrollmentRepository.save(newEnrollment);
    }
}
