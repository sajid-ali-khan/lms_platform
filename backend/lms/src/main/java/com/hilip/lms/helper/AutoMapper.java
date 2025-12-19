package com.hilip.lms.helper;

import com.hilip.lms.dtos.OrgUnitTypeResponse;
import com.hilip.lms.dtos.TenantOrgUnitTypeResponse;
import com.hilip.lms.dtos.TenantResponse;
import com.hilip.lms.models.OrgUnitType;
import com.hilip.lms.models.Tenant;
import com.hilip.lms.models.TenantOrgUnitType;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AutoMapper {
    @Mapping(target = "parentName", source = "parentType.name")
    OrgUnitTypeResponse mapOrgUnitTypeToOrgUnitTypeResponse(OrgUnitType orgUnitType);

    @Mapping(target = "structure", source = "orgUnitTypes")
    TenantOrgUnitTypeResponse mapTenantOrgUnitTypeToTenantOrgUnitTypeResponse(TenantOrgUnitType tenantOrgUnitType);

    @Mapping(target = "admin", source = "admin.fullName")
    TenantResponse mapTenantToTenantResponse(Tenant tenant);

}
