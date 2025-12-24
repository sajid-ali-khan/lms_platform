package com.hilip.lms.models;

import com.hilip.lms.models.enums.TenantCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "tenants")
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TenantCategory category;

    @Column(nullable = false)
    private String name;

    @JoinColumn(name = "admin_user_id")
    @OneToOne
    private User admin = null;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.REMOVE)
    private List<OrgStructure> orgStructures = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.REMOVE)
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.REMOVE)
    private List<Course> courses = new ArrayList<>();
}
