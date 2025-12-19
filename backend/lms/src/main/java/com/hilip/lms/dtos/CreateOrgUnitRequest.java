package com.hilip.lms.dtos;

import jakarta.annotation.Nullable;

import java.util.Map;

public record CreateOrgUnitRequest(
        String tenantId,
        String orgUnitTypeId,
        String name,
        String parentOrgUnitId,
        Map<String, String> attributes
) {
}
