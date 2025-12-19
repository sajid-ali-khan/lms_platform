package com.hilip.lms.dtos;

import java.util.List;

public record TenantOrgUnitTypeResponse(
	String id,
	String name,
	List<OrgUnitTypeResponse> structure
){}