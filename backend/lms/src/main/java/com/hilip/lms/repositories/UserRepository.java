package com.hilip.lms.repositories;

import com.hilip.lms.models.Tenant;
import com.hilip.lms.models.User;
import com.hilip.lms.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String username);
    boolean existsByEmail(String username);
    boolean existsByEmailAndTenant(String email, Tenant tenant);
    List<User> findAllByTenant(Tenant tenant);

    List<User> findAllByTenantAndRole(Tenant tenant, UserRole userRole);
}
