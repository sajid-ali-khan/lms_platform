package com.hilip.lms.dtos.course.modules;

import java.util.Map;

public record ModuleResponse(
        String id,
        String moduleTitle,
        Map<String, String> lessons
) {
}
