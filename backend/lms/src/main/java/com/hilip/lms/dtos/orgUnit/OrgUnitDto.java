package com.hilip.lms.dtos.orgUnit;

import java.util.UUID;

public record OrgUnitDto(
        UUID id,
        String name,
        int level,
        UUID parentId
) {
}
