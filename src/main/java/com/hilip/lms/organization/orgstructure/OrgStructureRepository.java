package com.hilip.lms.organization.orgstructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hilip.lms.tenant.Tenant;

public interface OrgStructureRepository extends JpaRepository<OrgStructure, UUID>{
	boolean existsByNameAndTenant(String name, Tenant tenant);
	Optional<OrgStructure> findByTenantAndName(Tenant tenant,String name);
}