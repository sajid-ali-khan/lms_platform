package com.hilip.lms.dtos.orgStructures;

import java.util.List;

public record OrgStructureResponseBasic(
        String id,
        String name,
        List<String> structure
){}
