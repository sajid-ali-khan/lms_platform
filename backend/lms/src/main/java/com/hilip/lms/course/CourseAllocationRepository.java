package com.hilip.lms.course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CourseAllocationRepository extends JpaRepository<CourseAllocation, UUID> {

    List<CourseAllocation> findAllByCourseId(UUID courseId);

    boolean existsByCourseIdAndOrgUnitId(UUID courseId, UUID orgUnitId);

    void deleteByCourseIdAndOrgUnitId(UUID courseId, UUID orgUnitId);

    @Query("""
            SELECT DISTINCT ca.course FROM CourseAllocation ca
            LEFT JOIN FETCH ca.course.thumbnailFile
            WHERE ca.orgUnit.id IN :orgUnitIds
            AND ca.course.status = 'ACTIVE'
            """)
    List<Course> findDistinctCoursesByOrgUnitIds(List<UUID> orgUnitIds);
}
