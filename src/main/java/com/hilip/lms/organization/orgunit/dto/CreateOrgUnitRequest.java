package com.hilip.lms.organization.orgunit.dto;

import java.util.Map;

public record CreateOrgUnitRequest(
        String orgUnitTypeId,
        String name,
        String parentOrgUnitId,
        Map<String, String> attributes
) {
}
