package com.hilip.lms.organization.orgunittype.dto;

public record OrgUnitTypeResponse(
        String id,
        String name,
        Integer level,
        String parentName
) {
}
