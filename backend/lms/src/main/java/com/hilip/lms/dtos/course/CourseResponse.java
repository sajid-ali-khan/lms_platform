package com.hilip.lms.dtos.course;

public record CourseResponse(
        String id,
        String title,
        String description,
        String thumbnailId,
        Boolean isPublished
) {
}
