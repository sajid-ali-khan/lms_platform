package com.hilip.lms.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
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

    @JoinColumn(nullable = false, name = "org_structure_id")
    @ManyToOne
    private OrgStructure orgStructure;

    @Column(nullable = false, name = "level_index")
    private Integer level;

    @OneToMany(mappedBy = "type", cascade = CascadeType.REMOVE)
    private List<OrgUnit> orgUnits = new ArrayList<>();

    @OneToOne(mappedBy = "parentType", cascade = CascadeType.REMOVE)
    private OrgUnitType childType;
}
