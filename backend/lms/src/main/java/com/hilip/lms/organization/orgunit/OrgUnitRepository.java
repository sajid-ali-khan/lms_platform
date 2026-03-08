package com.hilip.lms.organization.orgunit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hilip.lms.organization.orgstructure.OrgStructure;

import java.util.List;
import java.util.UUID;

public interface OrgUnitRepository extends JpaRepository<OrgUnit, UUID> {

    @Query("""
        select ou from OrgUnit ou
        where ou.tenant.id = :tenantId
        and ou.type.orgStructure.id = :structureId
        and ou.type.id = :typeId
""")
    List<OrgUnit> findByTenantIdAndStructureIdAndTypeId(UUID tenantId, UUID structureId, UUID typeId);

    @Query("""
        select ou from OrgUnit ou
        where ou.tenant.id = :tenantId
        and ou.type.orgStructure.id = :structureId
        and ou.type.id = :typeId
        and ou.parentUnit.id = :parentOrgUnitId
""")
    List<OrgUnit> findByTenantIdAndStructureIdAndTypeIdAndParentOrgUnitId(
            UUID tenantId,
            UUID structureId,
            UUID typeId,
            UUID parentOrgUnitId);

    List<OrgUnit> findAllByOrgStructure(OrgStructure orgStructure);

}
