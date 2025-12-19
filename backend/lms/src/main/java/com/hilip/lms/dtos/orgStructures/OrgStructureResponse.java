package com.hilip.lms.dtos.orgStructures;

import com.hilip.lms.dtos.orgUnitType.OrgUnitTypeResponse;

import java.util.List;

public record OrgStructureResponse(
	String id,
	String name,
	List<OrgUnitTypeResponse> structure
){}