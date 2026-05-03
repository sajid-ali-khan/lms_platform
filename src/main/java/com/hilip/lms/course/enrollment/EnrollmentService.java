package com.hilip.lms.course.enrollment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hilip.lms.course.CourseRepository;
import com.hilip.lms.course.enrollment.dto.EnrollmentRequest;
import com.hilip.lms.course.enrollment.dto.EnrollmentResponse;
import com.hilip.lms.organization.orgunit.OrgUnit;
import com.hilip.lms.user.User;
import com.hilip.lms.user.UserRepository;
import com.hilip.lms.user.userorgunit.UserOrgUnit;
import com.hilip.lms.user.userorgunit.UserOrgUnitRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final UserOrgUnitRepository userOrgUnitRepository;

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

    public List<EnrollmentResponse> getEnrollmentsByCourse(String courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findAllByCourseIdWithLearner(UUID.fromString(courseId));

        return enrollments.stream().map(e -> {
            User learner = e.getLearner();
            String orgPath = buildPrimaryOrgPath(learner);
            return new EnrollmentResponse(
                    e.getId().toString(),
                    learner.getId().toString(),
                    learner.getFullName(),
                    learner.getEmail(),
                    orgPath,
                    e.getStatus(),
                    e.getEnrolledAt().toString()
            );
        }).toList();
    }

    private String buildPrimaryOrgPath(User user) {
        List<UserOrgUnit> assignments = userOrgUnitRepository.findAllByUserId(user.getId());
        if (assignments.isEmpty()) return "—";
        // Use primary assignment if available, otherwise first
        UserOrgUnit target = assignments.stream()
                .filter(UserOrgUnit::isPrimary)
                .findFirst()
                .orElse(assignments.get(0));
        return buildOrgUnitPath(target.getOrgUnit());
    }

    private String buildOrgUnitPath(OrgUnit orgUnit) {
        List<String> parts = new ArrayList<>();
        OrgUnit current = orgUnit;
        while (current != null) {
            parts.add(current.getName());
            current = current.getParentUnit();
        }
        Collections.reverse(parts);
        return String.join(" / ", parts);
    }
}
