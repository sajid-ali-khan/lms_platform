package com.hilip.lms.organization.orgstructure.dto;

import java.util.List;

public record OrgStructureResponseBasic(
        String id,
        String name,
        List<String> structure
){}
