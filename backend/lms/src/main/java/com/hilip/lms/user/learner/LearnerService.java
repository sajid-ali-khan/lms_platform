package com.hilip.lms.user.learner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hilip.lms.course.Course;
import com.hilip.lms.course.CourseAllocationRepository;
import com.hilip.lms.course.CourseStatus;
import com.hilip.lms.course.dto.CourseResponse;
import com.hilip.lms.course.enrollment.EnrollmentRepository;
import com.hilip.lms.organization.orgunit.OrgUnit;
import com.hilip.lms.user.User;
import com.hilip.lms.user.UserRepository;
import com.hilip.lms.user.userorgunit.UserOrgUnit;
import com.hilip.lms.user.userorgunit.UserOrgUnitRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LearnerService {
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final UserOrgUnitRepository userOrgUnitRepository;
    private final CourseAllocationRepository courseAllocationRepository;

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

    /**
     * Returns courses allocated to any org unit that the learner belongs to
     * (including ancestor org units up the hierarchy).
     */
    public List<CourseResponse> getAvailableCourses(String learnerId) {
        User learner = userRepository.findById(UUID.fromString(learnerId))
                .orElseThrow(() -> new RuntimeException("Invalid learner"));

        // Get all org units the learner is assigned to
        List<UserOrgUnit> userOrgUnits = userOrgUnitRepository.findAllByUserId(learner.getId());

        if (userOrgUnits.isEmpty()) {
            // No org unit assignments → no filtered courses available
            return List.of();
        }

        // Collect all org unit IDs including ancestors (walk up the tree)
        List<UUID> allOrgUnitIds = new ArrayList<>();
        for (UserOrgUnit uou : userOrgUnits) {
            OrgUnit current = uou.getOrgUnit();
            while (current != null) {
                if (!allOrgUnitIds.contains(current.getId())) {
                    allOrgUnitIds.add(current.getId());
                }
                current = current.getParentUnit();
            }
        }

        // Find all courses allocated to these org units
        List<Course> courses = courseAllocationRepository.findDistinctCoursesByOrgUnitIds(allOrgUnitIds);

        return courses.stream()
                .map(this::mapCourseToCourseResponse)
                .toList();
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
