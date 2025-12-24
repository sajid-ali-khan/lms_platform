package com.hilip.lms.models;

import com.hilip.lms.models.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    private String code; // Course code like "CS101"

    @ManyToOne
    @JoinColumn(nullable = false)
    private Tenant tenant;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private User instructor;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CourseStatus status; // DRAFT, PUBLISHED, ARCHIVED

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
    private List<Module> modules = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
    private List<Enrollment> enrollments = new ArrayList<>(); // all enrollments for this course(super set to courseAllocations)

    @OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
    private List<CourseAllocation> courseAllocations = new ArrayList<>(); // made must for students
}

