package com.hilip.lms.dtos.orgUnit;

import java.util.Map;

public record OrgUnitDetails(
        String name,
        String type,
        String parent,
        Map<String, String> attributes
) {
}
