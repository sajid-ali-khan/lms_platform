package com.hilip.lms.organization.orgunit.dto;

import java.util.Map;

public record OrgUnitDetails(
        String name,
        String type,
        String parent,
        Map<String, String> attributes
) {
}
