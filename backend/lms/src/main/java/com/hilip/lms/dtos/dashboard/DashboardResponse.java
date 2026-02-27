package com.hilip.lms.dtos.dashboard;

import java.util.Map;

public record DashboardResponse(
        int userCount,
        int instructorCount,
        int learnerCount,
        int courseCount,
        int activeCourseCount,
        Map<String, Map<String, Integer>> orgUnitsCountMaps
) {
}
