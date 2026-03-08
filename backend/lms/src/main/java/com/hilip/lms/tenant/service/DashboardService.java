package com.hilip.lms.tenant.service;

import com.hilip.lms.course.CourseRepository;
import com.hilip.lms.course.CourseStatus;
import com.hilip.lms.organization.orgstructure.OrgStructure;
import com.hilip.lms.organization.orgunittype.OrgUnitType;
import com.hilip.lms.shared.exceptions.ResourceNotFoundException;
import com.hilip.lms.tenant.Tenant;
import com.hilip.lms.tenant.TenantRepository;
import com.hilip.lms.tenant.dto.DashboardResponse;
import com.hilip.lms.user.UserRepository;
import com.hilip.lms.user.UserRole;

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
        int userCount = userRepository.findAllByTenant(tenant).size();
        int instructorCount = userRepository.findAllByTenantAndRole(tenant, UserRole.INSTRUCTOR).size();
        int learnerCount = userRepository.findAllByTenantAndRole(tenant, UserRole.LEARNER).size();
        int courseCount = courseRepository.findAllByTenant(tenant).size();
        int activeCourseCount = courseRepository.findAllByTenantAndStatus(tenant, CourseStatus.ACTIVE).size();

        List<OrgStructure> orgStructures = tenant.getOrgStructures();
        Map<String, Map<String, Integer>> orgUnitCountMaps = new HashMap<>();

        for (OrgStructure orgStructure: orgStructures){
            final Map<String, Integer> orgUnitCount = new HashMap<>();
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
