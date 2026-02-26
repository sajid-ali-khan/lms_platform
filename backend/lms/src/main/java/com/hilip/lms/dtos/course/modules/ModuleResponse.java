package com.hilip.lms.dtos.course.modules;

import com.hilip.lms.dtos.course.lessons.LessonResponse;

import java.util.List;

public record ModuleResponse(
        String id,
        String title,
        Integer sequenceOrder,
        List<LessonResponse> lessons
) {
}
