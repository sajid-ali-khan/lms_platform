package com.hilip.lms.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "course_allocations")
public class CourseAllocation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(nullable = false)
    private OrgUnit orgUnit;

    private boolean isMandatory;
}
