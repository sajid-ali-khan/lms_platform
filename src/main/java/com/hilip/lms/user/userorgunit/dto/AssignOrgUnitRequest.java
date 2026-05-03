package com.hilip.lms.user.userorgunit.dto;

public record AssignOrgUnitRequest(
        String orgUnitId,
        boolean isPrimary
) {
}
