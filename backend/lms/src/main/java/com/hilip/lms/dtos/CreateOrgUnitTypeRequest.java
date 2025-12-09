package com.hilip.lms.dtos;

import java.util.List;

public record CreateOrgUnitTypeRequest(
        String tenantId,
        List<String> hierarchyLevels
) {
}
