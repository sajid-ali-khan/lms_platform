package com.hilip.lms.helper;

import com.hilip.lms.dtos.CreateOrgUnitRequest;
import com.hilip.lms.dtos.OrgUnitTypeResponse;
import com.hilip.lms.models.OrgUnit;
import com.hilip.lms.models.OrgUnitType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AutoMapper {
    @Mapping(target = "parentName", source = "parentType.name")
    OrgUnitTypeResponse mapOrgUnitTypeToResponse(OrgUnitType orgUnitType);

}
