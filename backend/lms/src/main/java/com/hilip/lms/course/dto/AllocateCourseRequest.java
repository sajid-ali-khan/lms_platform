package com.hilip.lms.course.dto;

public record AllocateCourseRequest(
        String orgUnitId,
        boolean isMandatory
) {
}
