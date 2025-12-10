package com.hilip.lms.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
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

    @OneToOne(mappedBy = "tenant", cascade = CascadeType.REMOVE)
    private User admin = null;
}
