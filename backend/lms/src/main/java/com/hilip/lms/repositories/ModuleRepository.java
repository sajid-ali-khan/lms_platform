package com.hilip.lms.repositories;

import com.hilip.lms.models.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ModuleRepository extends JpaRepository<Module, UUID> {

    @Query("SELECT m FROM Module m WHERE m.course.id = :courseId ORDER BY m.sequenceOrder")
    List<Module> findAllByCourseIdOrderBySequenceOrder(@Param("courseId") UUID courseId);

    @Query("SELECT COUNT(m) FROM Module m WHERE m.course.id = :courseId")
    int countByCourseId(@Param("courseId") UUID courseId);
}
