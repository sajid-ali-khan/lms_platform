package com.hilip.lms.organization.orgstructure.dto;

import java.util.List;

import com.hilip.lms.organization.orgunittype.dto.OrgUnitTypeResponse;

public record OrgStructureResponse(
	String id,
	String name,
	List<OrgUnitTypeResponse> structure
){}