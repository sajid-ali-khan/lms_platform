package com.hilip.lms.course.lesson.dto;

public record LessonResponse(
        String id,
        String title,
        String content,
        String type,
        Integer sequenceOrder,
        String resourceUrl,
        Boolean isPublished
) {
}
