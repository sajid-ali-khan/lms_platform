package com.hilip.lms.models;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
@Table(name = "modules")
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Course course;

    @Column(nullable = false, name = "sequence_order")
    private Integer sequenceOrder; // For ordering modules within a course

    @Column(nullable = false)
    private Boolean isPublished = false;
}
