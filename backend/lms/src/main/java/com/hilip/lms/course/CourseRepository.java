package com.hilip.lms.course;

import com.hilip.lms.tenant.Tenant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    List<Course> findAllByTenant(Tenant tenant);
    List<Course> findAllByTenantAndStatus(Tenant tenant, CourseStatus courseStatus);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.thumbnailFile WHERE c.tenant.id = :tenantId")
    List<Course> findAllByTenantIdWithThumbnail(@Param("tenantId") UUID tenantId);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.thumbnailFile WHERE c.id = :courseId")
    Course findByIdWithThumbnail(@Param("courseId") UUID courseId);
}
