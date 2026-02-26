package com.hilip.lms.dtos.course.lessons;

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
