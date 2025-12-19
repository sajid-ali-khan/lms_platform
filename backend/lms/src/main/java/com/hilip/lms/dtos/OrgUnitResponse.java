package com.hilip.lms.dtos;

import java.util.Map;

public record OrgUnitResponse(
        String id,
        String name,
        String type,
        String parentName,
        Map<String, String> attributes
) {
}
