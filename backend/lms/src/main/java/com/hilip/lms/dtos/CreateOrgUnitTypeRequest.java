package com.hilip.lms.dtos;

import java.util.List;

public record CreateOrgUnitTypeRequest(
        String name,
        List<String> hierarchyLevels
) {
}
