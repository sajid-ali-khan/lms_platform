package com.hilip.lms.course.dto;

import com.hilip.lms.course.CourseStatus;

public record UpdateCourseRequest(
        String title,
        String description,
        String instructorId,
        CourseStatus status
) {
}
