package com.hilip.lms.models;

import com.hilip.lms.models.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String email;

    private String fullName;

    @Column(nullable = false)
    private String passwordHash;

    @ManyToOne
    @JoinColumn
    private Tenant tenant;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    UserRole role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.REMOVE)
    private List<Course> instructedCourses;

    @OneToMany(mappedBy = "learner", cascade = CascadeType.REMOVE)
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<UserOrgUnit> orgUnits = new ArrayList<>();
}
