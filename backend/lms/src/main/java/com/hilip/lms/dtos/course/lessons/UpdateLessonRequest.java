package com.hilip.lms.dtos.course.lessons;

import com.hilip.lms.models.enums.LessonType;

public record UpdateLessonRequest(
        String title,
        String content,
        LessonType type,
        String resourceUrl,
        Boolean isPublished
) {
}
