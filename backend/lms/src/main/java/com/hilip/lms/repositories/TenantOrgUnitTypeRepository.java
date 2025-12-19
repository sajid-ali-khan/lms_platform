package com.hilip.lms.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hilip.lms.models.Tenant;
import com.hilip.lms.models.TenantOrgUnitType;

public interface TenantOrgUnitTypeRepository extends JpaRepository<TenantOrgUnitType, UUID>{
	boolean existsByNameAndTenant(String name, Tenant tenant);
}