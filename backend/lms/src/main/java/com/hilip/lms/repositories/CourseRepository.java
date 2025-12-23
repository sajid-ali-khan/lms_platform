package com.hilip.lms.repositories;

import com.hilip.lms.models.Course;
import com.hilip.lms.models.CourseStatus;
import com.hilip.lms.models.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    List<Course> findAllByTenant(Tenant tenant);
    List<Course> findAllByTenantAndStatus(Tenant tenant, CourseStatus courseStatus);
}
