package com.hilip.lms.course.lesson;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

import com.hilip.lms.course.module.Module;

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
