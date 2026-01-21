package com.hilip.lms.dtos.course;

import com.hilip.lms.models.enums.CourseStatus;

import java.util.Map;

public record CourseResponse(
        String id,
        String title,
        String description,
        String thumbnailId,
        CourseStatus currentStatus,
        Map<CourseStatus, Boolean> statusOptions
) {
}
