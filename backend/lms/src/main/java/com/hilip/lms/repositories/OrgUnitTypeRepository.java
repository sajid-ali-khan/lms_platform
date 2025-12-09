package com.hilip.lms.repositories;

import com.hilip.lms.models.OrgUnitType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrgUnitTypeRepository extends JpaRepository<OrgUnitType, UUID> {
    List<OrgUnitType> findByTenantId(UUID tenantId);
}
