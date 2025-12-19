package com.hilip.lms.dtos;

import com.hilip.lms.dtos.orgUnitType.OrgUnitTypeResponse;

import java.util.List;

public record OrgStructureResponse(
	String id,
	String name,
	List<OrgUnitTypeResponse> structure
){}