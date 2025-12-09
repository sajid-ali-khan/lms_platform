package com.hilip.lms.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "org_unit_types")
public class OrgUnitType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private  UUID id;

    private String name;

    @JoinColumn(name = "parent_type_id")
    @ManyToOne
    private OrgUnitType parentType;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Tenant tenant;

    @Column(nullable = false, name = "level_index")
    private Integer level;
}
