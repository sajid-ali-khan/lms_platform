package com.hilip.lms.dtos.orgUnitType;

import java.util.List;

public record CreateOrgUnitTypeRequest(
        String name,
        List<String> hierarchyLevels
) {
}
