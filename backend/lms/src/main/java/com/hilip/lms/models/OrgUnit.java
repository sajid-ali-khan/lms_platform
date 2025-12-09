package com.hilip.lms.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "org_units")
public class OrgUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private OrgUnitType type;

    @ManyToOne
    @JoinColumn(name = "parent_unit_id")
    private OrgUnit parentUnit;

    @ManyToOne
    @JoinColumn
    private Tenant tenant;
}
