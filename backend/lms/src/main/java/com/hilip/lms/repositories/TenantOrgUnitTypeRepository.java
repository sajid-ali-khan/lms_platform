package com.hilip.lms.repositories;

import java.util.UUID;

import com.hilip.lms.models.OrgStructure;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hilip.lms.models.Tenant;

public interface TenantOrgUnitTypeRepository extends JpaRepository<OrgStructure, UUID>{
	boolean existsByNameAndTenant(String name, Tenant tenant);
}