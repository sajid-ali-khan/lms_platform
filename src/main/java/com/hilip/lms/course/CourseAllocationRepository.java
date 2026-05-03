package com.hilip.lms.course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CourseAllocationRepository extends JpaRepository<CourseAllocation, UUID> {

    List<CourseAllocation> findAllByCourseId(UUID courseId);

    boolean existsByCourseIdAndOrgUnitId(UUID courseId, UUID orgUnitId);

    void deleteByCourseIdAndOrgUnitId(UUID courseId, UUID orgUnitId);

    long countByOrgUnitId(UUID orgUnitId);

    @Query("SELECT ca.course FROM CourseAllocation ca LEFT JOIN FETCH ca.course.thumbnailFile WHERE ca.orgUnit.id = :orgUnitId")
    List<Course> findCoursesByOrgUnitId(UUID orgUnitId);

    @Query("""
            SELECT DISTINCT ca.course FROM CourseAllocation ca
            LEFT JOIN FETCH ca.course.thumbnailFile
            WHERE ca.orgUnit.id IN :orgUnitIds
            AND ca.course.status = 'ACTIVE'
            """)
    List<Course> findDistinctCoursesByOrgUnitIds(List<UUID> orgUnitIds);

    @Query("SELECT COUNT(DISTINCT ca.course.id) FROM CourseAllocation ca WHERE ca.orgUnit.id IN :orgUnitIds")
    long countDistinctCoursesByOrgUnitIds(List<UUID> orgUnitIds);

    @Query("SELECT DISTINCT ca.course FROM CourseAllocation ca LEFT JOIN FETCH ca.course.thumbnailFile WHERE ca.orgUnit.id IN :orgUnitIds")
    List<Course> findDistinctCoursesByOrgUnitIdsNoStatus(List<UUID> orgUnitIds);
}
