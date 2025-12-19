package com.hilip.lms.repositories;

import com.hilip.lms.models.OrgUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OrgUnitRepository extends JpaRepository<OrgUnit, UUID> {
    @Query("""
        select ou from OrgUnit ou
        where ou.tenant.id = :tenantId
        and ou.type.id = :orgUnitTypeId
""")
    List<OrgUnit> findByTenantAndStructureTypeAndOrgUnitType(String tenantId, String orgUnitTypeId);
}
