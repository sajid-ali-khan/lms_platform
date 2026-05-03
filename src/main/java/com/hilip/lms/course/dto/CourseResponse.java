package com.hilip.lms.course.dto;

import java.util.Map;

import com.hilip.lms.course.CourseStatus;

public record CourseResponse(
        String id,
        String title,
        String description,
        String thumbnailId,
        CourseStatus currentStatus,
        Map<CourseStatus, Boolean> statusOptions
) {
}
