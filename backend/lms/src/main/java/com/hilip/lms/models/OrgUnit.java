package com.hilip.lms.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.*;

@Getter
@Setter
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

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> attributes = new HashMap<>();

    @OneToMany(mappedBy = "parentUnit", cascade = CascadeType.REMOVE)
    private List<OrgUnit> childUnits = new ArrayList<>();
}
