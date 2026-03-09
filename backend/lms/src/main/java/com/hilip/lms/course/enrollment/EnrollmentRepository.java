package com.hilip.lms.course.enrollment;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hilip.lms.course.Course;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID>{

    @Query("""
            SELECT e.course FROM Enrollment e
            LEFT JOIN FETCH e.course.thumbnailFile
            WHERE e.learner.id = :learnerId
            """)
    List<Course> findAllEnrolledCoursesByLearnerId(UUID learnerId);

    boolean existsByLearnerIdAndCourseId(UUID learnerId, UUID courseId);
}
