package com.hilip.lms.repositories;

import com.hilip.lms.models.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {

    @Query("SELECT l FROM Lesson l WHERE l.module.id = :moduleId ORDER BY l.sequenceOrder")
    List<Lesson> findAllByModuleIdOrderBySequenceOrder(@Param("moduleId") UUID moduleId);

    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.module.id = :moduleId")
    int countByModuleId(@Param("moduleId") UUID moduleId);
}
