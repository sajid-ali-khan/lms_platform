package com.hilip.lms.course.lesson.dto;

import com.hilip.lms.course.lesson.LessonType;

public record UpdateLessonRequest(
        String title,
        String content,
        LessonType type,
        String resourceUrl,
        Boolean isPublished
) {
}
