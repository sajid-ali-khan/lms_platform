package com.hilip.lms.services;

import com.hilip.lms.dtos.dashboard.DashboardResponse;
import com.hilip.lms.exceptions.ResourceNotFoundException;
import com.hilip.lms.models.*;
import com.hilip.lms.models.enums.CourseStatus;
import com.hilip.lms.models.enums.UserRole;
import com.hilip.lms.repositories.CourseRepository;
import com.hilip.lms.repositories.TenantRepository;
import com.hilip.lms.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DashboardService {
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final CourseRepository courseRepository;

    public DashboardResponse getDashboardDetails(String tenantId){
        Tenant tenant = tenantRepository.findById(UUID.fromString(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found."));
//        userCount = findAllByTenant
        int userCount = userRepository.findAllByTenant(tenant).size();
        int instructorCount = userRepository.findAllByTenantAndRole(tenant, UserRole.INSTRUCTOR).size();
        int learnerCount = userRepository.findAllByTenantAndRole(tenant, UserRole.LEARNER).size();
        int courseCount = courseRepository.findAllByTenant(tenant).size();
        int activeCourseCount = courseRepository.findAllByTenantAndStatus(tenant, CourseStatus.ACTIVE).size();

        List<OrgStructure> orgStructures = tenant.getOrgStructures();
        Map<String, Map<String, Integer>> orgUnitCountMaps = new HashMap<>();

        for (OrgStructure orgStructure: orgStructures){
            final Map<String, Integer> orgUnitCount = new HashMap<>();
//            top level
            OrgUnitType topLevelType = orgStructure.getOrgUnitTypes().stream()
                    .filter(orgUnitType -> orgUnitType.getLevel() == 0)
                    .toList().getFirst();
            int lastLevel = orgStructure.getOrgUnitTypes().size() - 1;
            OrgUnitType bottomLevelType = orgStructure.getOrgUnitTypes().stream()
                    .filter(orgUnitType -> orgUnitType.getLevel() == lastLevel)
                    .toList().getFirst();

            orgUnitCount.put(topLevelType.getName(), topLevelType.getOrgUnits().size());
            orgUnitCount.put(bottomLevelType.getName(), bottomLevelType.getOrgUnits().size());

            orgUnitCountMaps.put(orgStructure.getName(), orgUnitCount);
        }

        return new DashboardResponse(
                userCount,
                instructorCount,
                learnerCount,
                courseCount,
                activeCourseCount,
                orgUnitCountMaps
        );
    }
}
