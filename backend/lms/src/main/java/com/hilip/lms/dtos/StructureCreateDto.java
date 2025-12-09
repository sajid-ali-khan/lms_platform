package com.hilip.lms.dtos;

import java.util.List;

public record StructureCreateDto(
        String tenantId,
        List<String> hierarchyLevels
) {
}
