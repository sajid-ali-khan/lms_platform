package com.hilip.lms.course.dto;

public record CourseAllocationResponse(
        String id,
        String orgUnitId,
        String orgUnitName,
        String orgUnitPath,
        boolean isMandatory
) {
}
