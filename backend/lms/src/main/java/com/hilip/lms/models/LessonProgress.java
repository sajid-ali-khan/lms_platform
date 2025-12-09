package com.hilip.lms.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "lesson_progress")
public class LessonProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Lesson lesson;

    @Column(nullable = false)
    private Boolean isCompleted = false;

    private LocalDateTime completedAt;

    private LocalDateTime lastAccessedAt;

    private Integer timeSpentMinutes;
}
