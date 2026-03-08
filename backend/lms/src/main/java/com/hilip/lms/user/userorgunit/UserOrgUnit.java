package com.hilip.lms.user.userorgunit;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

import com.hilip.lms.organization.orgunit.*;
import com.hilip.lms.user.User;

@Getter
@Setter
@Entity
@Table(name = "user_org_units")
public class UserOrgUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private OrgUnit orgUnit;

    private boolean isPrimary;
}
