package com.hilip.lms.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hilip.lms.models.Course;
import com.hilip.lms.models.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID>{

    @Query("""
            SELECT e.course FROM Enrollment e
            LEFT JOIN FETCH e.course.thumbnailFile
            WHERE e.learner.id = :learnedId
            """)
    List<Course> findAllEnrolledCoursesByLearnerId(UUID learnedId);
}
