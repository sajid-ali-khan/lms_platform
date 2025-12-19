package com.hilip.lms.dtos.orgUnitType;

public record OrgUnitTypeResponse(
        String id,
        String name,
        Integer level,
        String parentName
) {
}
