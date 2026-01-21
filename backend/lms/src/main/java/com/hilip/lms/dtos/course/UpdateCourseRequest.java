package com.hilip.lms.dtos.course;

import com.hilip.lms.models.enums.CourseStatus;

public record UpdateCourseRequest(
        String title,
        String description,
        String instructorId,
        CourseStatus status
) {
}
