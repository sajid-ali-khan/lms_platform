package com.hilip.lms.models;

import com.hilip.lms.models.enums.LessonType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "lessons")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(length = 5000)
    private String content;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Module module;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LessonType type = LessonType.VIDEO;

    @Column(nullable = false, name = "sequence_order")
    private Integer sequenceOrder;

    private String resourceUrl;

    @Column(nullable = false)
    private Boolean isPublished = false;
}
