package com.hilip.lms.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "modules")
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Course course;

    @Column(nullable = false, name = "sequence_order")
    private Integer sequenceOrder;

    @Column(nullable = false)
    private Boolean isPublished = false;

    @OneToMany(mappedBy = "module", cascade = {CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE})
    private java.util.List<Lesson> lessons = new java.util.ArrayList<>();
}
