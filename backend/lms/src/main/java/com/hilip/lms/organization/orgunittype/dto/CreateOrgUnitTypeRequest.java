package com.hilip.lms.organization.orgunittype.dto;

import java.util.List;

public record CreateOrgUnitTypeRequest(
        String name,
        List<String> hierarchyLevels
) {
}
