package com.hilip.lms.repositories;

import com.hilip.lms.models.Tenant;
import com.hilip.lms.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByUsernameAndTenant(String username, Tenant tenant);
    List<User> findAllByTenant(Tenant tenant);
}
