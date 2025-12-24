package com.hilip.lms.dtos.course;

import com.hilip.lms.models.enums.CourseStatus;

public record CreateCourseRequest(
        String title,
        String description,
        String instructorId,
        CourseStatus visibility,
        String coverPicturePath
) {
}
