package com.hilip.lms.dtos.orgUnit;

import java.util.Map;

public record CreateOrgUnitRequest(
        String orgUnitTypeId,
        String name,
        String parentOrgUnitId,
        Map<String, String> attributes
) {
}
