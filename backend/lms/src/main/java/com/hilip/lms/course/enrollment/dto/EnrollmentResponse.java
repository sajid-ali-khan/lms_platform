package com.hilip.lms.course.enrollment.dto;

public record EnrollmentResponse(
        String id,
        String learnerId,
        String learnerName,
        String learnerEmail,
        String orgUnitPath,
        String status,
        String enrolledAt
) {
}
