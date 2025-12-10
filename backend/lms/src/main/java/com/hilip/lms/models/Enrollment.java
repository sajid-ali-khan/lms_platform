package com.hilip.lms.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "enrollments")
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User learner;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "course_allocation_id")
    private CourseAllocation courseAllocation;

    @Column(nullable = false)
    private LocalDateTime enrolledAt;

    private LocalDateTime completedAt;

    @Column(nullable = false)
    private String status; // ACTIVE, COMPLETED, DROPPED

    private Double progress; // 0.0 to 100.0

    @Column(name = "final_grade")
    private Double finalGrade;
}
