package com.hilip.lms.course.enrollment.dto;

public record EnrollmentRequest(
    String courseId,
    String learnerId
) {
    
}
