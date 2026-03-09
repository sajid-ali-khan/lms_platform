package com.hilip.lms.user.userorgunit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserOrgUnitRepository extends JpaRepository<UserOrgUnit, UUID> {

    List<UserOrgUnit> findAllByUserId(UUID userId);

    boolean existsByUserIdAndOrgUnitId(UUID userId, UUID orgUnitId);

    void deleteByUserIdAndOrgUnitId(UUID userId, UUID orgUnitId);
}
