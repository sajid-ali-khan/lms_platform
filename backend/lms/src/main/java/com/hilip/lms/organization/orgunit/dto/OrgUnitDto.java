package com.hilip.lms.organization.orgunit.dto;

import java.util.UUID;

public record OrgUnitDto(
        UUID id,
        String name,
        int level,
        UUID parentId
) {
}
