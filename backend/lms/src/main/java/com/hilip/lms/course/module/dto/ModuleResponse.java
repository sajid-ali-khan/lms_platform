package com.hilip.lms.course.module.dto;

import java.util.List;

import com.hilip.lms.course.lesson.dto.LessonResponse;

public record ModuleResponse(
        String id,
        String title,
        Integer sequenceOrder,
        List<LessonResponse> lessons
) {
}
