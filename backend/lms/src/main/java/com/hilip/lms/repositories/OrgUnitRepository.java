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
        and ou.type.orgStructure.name = :structureName
        and ou.type.name = :typeName
""")
    List<OrgUnit> findByTenantAndStructureAndType(UUID tenantId, String structureName, String typeName);

    @Query("""
        select ou from OrgUnit ou
        where ou.tenant.id = :tenantId
        and ou.type.orgStructure.name = :structureName
        and ou.type.name = :typeName
        and ou.parentUnit.id = :parentOrgUnitId
""")
    List<OrgUnit> findByTenantAndStructureTypeAndParentOrgUnitId(UUID tenantId, String structureName, String typeName, UUID parentOrgUnitId);
}
