package com.hilip.lms.shared.helper;

import com.hilip.lms.course.lesson.dto.LessonResponse;
import com.hilip.lms.organization.orgstructure.OrgStructure;
import com.hilip.lms.organization.orgstructure.dto.OrgStructureResponse;
import com.hilip.lms.organization.orgstructure.dto.OrgStructureResponseBasic;
import com.hilip.lms.organization.orgunit.*;
import com.hilip.lms.organization.orgunit.dto.*;
import com.hilip.lms.organization.orgunittype.OrgUnitType;
import com.hilip.lms.organization.orgunittype.dto.OrgUnitTypeResponse;
import com.hilip.lms.tenant.Tenant;
import com.hilip.lms.tenant.dto.TenantAndAdminResponse;
import com.hilip.lms.tenant.dto.TenantResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AutoMapper {
    @Mapping(target = "parentName", source = "parentType.name")
    OrgUnitTypeResponse mapOrgUnitTypeToOrgUnitTypeResponse(OrgUnitType orgUnitType);

    @Mapping(target = "structure", expression = "java(orgStructure.getOrgUnitTypes().stream().map(OrgUnitType::getName).toList())")
    OrgStructureResponseBasic mapOrgStructureToOrgStructureResponseBasic(OrgStructure orgStructure);

    @Mapping(target = "structure", source = "orgUnitTypes")
    OrgStructureResponse mapOrgStructureToOrgStructureResponse(OrgStructure orgStructure);



    @Mapping(target = "admin", source = "admin.fullName")
    TenantResponse mapTenantToTenantResponse(Tenant tenant);

    OrgUnitResponse mapOrgUnitToOrgUnitResponse(OrgUnit orgUnit);

    @Mapping(target = "tenantName", source = "tenant.name")
    @Mapping(target = "tenantCategory", source = "tenant.category")
    @Mapping(target = "adminEmail", source = "tenant.admin.email")
    @Mapping(target = "adminPassword", source = "adminPassword")
    TenantAndAdminResponse mapTenantToTenantAndAdminResponse(Tenant tenant, String adminPassword);

    @Mapping(target = "parentId", source = "parentUnit.id")
    @Mapping(target = "level", source = "type.level")
    OrgUnitDto mapOrgUnitToOrgUnitDto(OrgUnit orgUnit);

    @Mapping(target = "parent", source = "parentUnit.name")
    @Mapping(target = "type", source = "type.name")
    OrgUnitDetails mapOrgUnitToOrgUnitDetails(OrgUnit orgUnit);


    LessonResponse mapLessonToLessonResponse(com.hilip.lms.course.lesson.Lesson lesson);
}
