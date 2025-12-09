package com.hilip.lms.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "user_org_units")
public class UserOrgUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn
    private User user;

    @OneToOne
    @JoinColumn(name = "org_unit_id")
    private OrgUnit orgUnit;
}
