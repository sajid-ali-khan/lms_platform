package com.hilip.lms.user.userorgunit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserOrgUnitRepository extends JpaRepository<UserOrgUnit, UUID> {

    List<UserOrgUnit> findAllByUserId(UUID userId);

    boolean existsByUserIdAndOrgUnitId(UUID userId, UUID orgUnitId);

    void deleteByUserIdAndOrgUnitId(UUID userId, UUID orgUnitId);

    void deleteByUserId(UUID userId);

    long countByOrgUnitId(UUID orgUnitId);

    long countByOrgUnitIdAndUserRole(UUID orgUnitId, com.hilip.lms.user.UserRole role);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(DISTINCT uou.user.id) FROM UserOrgUnit uou WHERE uou.orgUnit.id IN :orgUnitIds")
    long countDistinctUsersByOrgUnitIds(List<UUID> orgUnitIds);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(DISTINCT uou.user.id) FROM UserOrgUnit uou WHERE uou.orgUnit.id IN :orgUnitIds AND uou.user.role = :role")
    long countDistinctUsersByOrgUnitIdsAndRole(List<UUID> orgUnitIds, com.hilip.lms.user.UserRole role);
}
