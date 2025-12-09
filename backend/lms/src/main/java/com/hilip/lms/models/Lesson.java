package com.hilip.lms.models;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
@Table(name = "lessons")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Module module;

    @Column(nullable = false)
    private String type; // VIDEO, TEXT, DOCUMENT, QUIZ, ASSIGNMENT

    @Column(nullable = false, name = "sequence_order")
    private Integer sequenceOrder;

    private String resourceUrl; // For videos, documents, etc.

    @Column(nullable = false)
    private Boolean isPublished = false;

    private Integer durationMinutes;
}
