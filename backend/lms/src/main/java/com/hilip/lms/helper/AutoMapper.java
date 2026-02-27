package com.hilip.lms.helper;

import com.hilip.lms.dtos.course.lessons.LessonResponse;
import com.hilip.lms.dtos.orgStructures.OrgStructureResponseBasic;
import com.hilip.lms.dtos.orgUnit.OrgUnitDetails;
import com.hilip.lms.dtos.orgUnit.OrgUnitDto;
import com.hilip.lms.dtos.orgUnit.OrgUnitResponse;
import com.hilip.lms.dtos.orgUnitType.OrgUnitTypeResponse;
import com.hilip.lms.dtos.orgStructures.OrgStructureResponse;
import com.hilip.lms.dtos.tenant.TenantAndAdminResponse;
import com.hilip.lms.dtos.tenant.TenantResponse;
import com.hilip.lms.models.OrgStructure;
import com.hilip.lms.models.OrgUnit;
import com.hilip.lms.models.OrgUnitType;
import com.hilip.lms.models.Tenant;

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


    LessonResponse mapLessonToLessonResponse(com.hilip.lms.models.Lesson lesson);
}
