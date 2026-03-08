package com.hilip.lms.dtos.course;

public record EnrollmentRequest(
    String courseId,
    String learnerId
) {
    
}
