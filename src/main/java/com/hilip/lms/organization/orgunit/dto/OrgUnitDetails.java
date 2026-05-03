package com.hilip.lms.organization.orgunit.dto;

import java.util.List;
import java.util.Map;

public record OrgUnitDetails(
        String id,
        String name,
        String type,
        String parent,
        Map<String, String> attributes,
        long userCount,
        long courseCount,
        long facultyCount,
        List<OrgUnitCourseResponse> courses
) {
    public record OrgUnitCourseResponse(String id, String title, String status) {}
}
