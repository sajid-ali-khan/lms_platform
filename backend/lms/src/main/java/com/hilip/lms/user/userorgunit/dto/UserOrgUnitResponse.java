package com.hilip.lms.user.userorgunit.dto;

public record UserOrgUnitResponse(
        String id,
        String orgUnitId,
        String orgUnitName,
        String orgUnitPath,
        boolean isPrimary
) {
}
