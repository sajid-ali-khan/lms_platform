package com.hilip.lms.organization.orgunit;

import com.hilip.lms.organization.orgstructure.OrgStructure;
import com.hilip.lms.organization.orgstructure.OrgStructureRepository;
import com.hilip.lms.organization.orgunit.dto.CreateOrgUnitRequest;
import com.hilip.lms.organization.orgunit.dto.OrgUnitDetails;
import com.hilip.lms.organization.orgunit.dto.OrgUnitDto;
import com.hilip.lms.organization.orgunit.dto.OrgUnitResponse;
import com.hilip.lms.organization.orgunittype.OrgUnitTypeRepository;
import com.hilip.lms.shared.exceptions.ResourceNotFoundException;
import com.hilip.lms.shared.helper.AutoMapper;
import com.hilip.lms.tenant.Tenant;
import com.hilip.lms.tenant.TenantRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class OrgUnitService {
    private final OrgUnitRepository orgUnitRepository;
    private final OrgUnitTypeRepository orgUnitTypeRepository;
    private final TenantRepository tenantRepository;
    private final AutoMapper autoMapper;
    private final OrgStructureRepository orgStructureRepository;
    private final com.hilip.lms.user.userorgunit.UserOrgUnitRepository userOrgUnitRepository;
    private final com.hilip.lms.course.CourseAllocationRepository courseAllocationRepository;

    public void createOrgUnit(String tenantId, CreateOrgUnitRequest request) {
        boolean parentNull = request.parentOrgUnitId() == null || request.parentOrgUnitId().isBlank();

        var orgUnitType = orgUnitTypeRepository.findById(UUID.fromString(request.orgUnitTypeId()))
                .orElseThrow(() -> new ResourceNotFoundException("Org unit type not found for id: " + request.orgUnitTypeId()));

        if (parentNull && orgUnitType.getParentType() != null){
            throw new IllegalArgumentException("Parent unit should not be null because " + orgUnitType.getName() + " requires a parent unit of time " + orgUnitType.getParentType().getName());
        }

        if (!parentNull && orgUnitType.getParentType() == null){
            throw new IllegalArgumentException("Parent unit must be null for org unit type " + orgUnitType.getName());
        }

        Tenant tenant = tenantRepository.findById(UUID.fromString(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
        OrgStructure orgStructure = orgUnitType.getOrgStructure();

        var newOrgUnit = new OrgUnit();
        newOrgUnit.setName(request.name());
        newOrgUnit.setType(orgUnitType);
        newOrgUnit.setTenant(tenant);
        newOrgUnit.setOrgStructure(orgStructure);
        newOrgUnit.setAttributes(request.attributes());

        if (!parentNull){
            OrgUnit parentUnit = orgUnitRepository.findById(UUID.fromString(request.parentOrgUnitId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Parent org unit not found for id: " + request.parentOrgUnitId()));
            newOrgUnit.setParentUnit(parentUnit);
        }

        orgUnitRepository.save(newOrgUnit);
    }

    public List<OrgUnitResponse> getOrgUnitsByTenantAndStructureAndType(String tenantId, String structureId, String typeId) {
        log.info("Getting org units for tenantId: {}, structureId: {}, typeId: {}", tenantId, structureId, typeId);
        var orgUnits = orgUnitRepository.findByTenantIdAndStructureIdAndTypeId(
                UUID.fromString(tenantId),
                UUID.fromString(structureId),
                UUID.fromString(typeId)
        );
        return orgUnits.stream()
                .map(autoMapper::mapOrgUnitToOrgUnitResponse)
                .toList();
    }

    public List<OrgUnitResponse> getOrgUnitsByTenantStructureTypeAndParentUnit(String tenantId, String structureId, String typeId, String parentUnitId) {
        log.info("Getting org units for tenantId: {}, structureId: {}, typeId: {} and parentUnitId: {}", tenantId, structureId, typeId, parentUnitId);
        var orgUnits = orgUnitRepository.findByTenantIdAndStructureIdAndTypeIdAndParentOrgUnitId(
                UUID.fromString(tenantId),
                UUID.fromString(structureId),
                UUID.fromString(typeId),
                UUID.fromString(parentUnitId)
        );
        return orgUnits.stream()
                .map(autoMapper::mapOrgUnitToOrgUnitResponse)
                .toList();
    }

    public List<OrgUnitDto> getOrgUnitsTreeByTenantAndStructure(String tenantId, String structureId) {
        Tenant tenant = tenantRepository.findById(UUID.fromString(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        OrgStructure orgStructure = orgStructureRepository.findById(UUID.fromString(structureId))
                .orElseThrow(() -> new ResourceNotFoundException("Org Structure not found"));

        if (!orgStructure.getTenant().getId().equals(tenant.getId())) {
            throw new IllegalArgumentException("Org Structure does not belong to the specified tenant");
        }

        return orgUnitRepository.findAllByOrgStructure(orgStructure)
                .stream()
                .map(autoMapper::mapOrgUnitToOrgUnitDto)
                .toList();
    }

    public OrgUnitDetails getOrgUnitDetails(String orgUnitId){
        OrgUnit orgUnit = orgUnitRepository.findById(UUID.fromString(orgUnitId))
                .orElseThrow(() -> new ResourceNotFoundException("Org Unit not found"));

        List<OrgUnit> allUnits = orgUnitRepository.findAllByOrgStructure(orgUnit.getOrgStructure());
        Map<UUID, List<OrgUnit>> parentToChildren = new HashMap<>();
        for (OrgUnit u : allUnits) {
            if (u.getParentUnit() != null) {
                parentToChildren.computeIfAbsent(u.getParentUnit().getId(), k -> new ArrayList<>()).add(u);
            }
        }

        List<UUID> relatedIds = new ArrayList<>();
        OrgUnit current = orgUnit;
        while (current != null) {
            relatedIds.add(current.getId());
            current = current.getParentUnit();
        }

        collectAllDescendantsInMemory(orgUnit.getId(), parentToChildren, relatedIds);

        List<UUID> uniqueIds = relatedIds.stream().distinct().toList();

        long userCount = userOrgUnitRepository.countDistinctUsersByOrgUnitIds(uniqueIds);
        long facultyCount = userOrgUnitRepository.countDistinctUsersByOrgUnitIdsAndRole(uniqueIds, com.hilip.lms.user.UserRole.INSTRUCTOR);
        long courseCount = courseAllocationRepository.countDistinctCoursesByOrgUnitIds(uniqueIds);

        List<com.hilip.lms.course.Course> courses = courseAllocationRepository.findDistinctCoursesByOrgUnitIdsNoStatus(uniqueIds);
        List<OrgUnitDetails.OrgUnitCourseResponse> courseResponses = courses.stream()
                .map(c -> new OrgUnitDetails.OrgUnitCourseResponse(c.getId().toString(), c.getTitle(), c.getStatus().name()))
                .toList();

        return new OrgUnitDetails(
                orgUnit.getId().toString(),
                orgUnit.getName(),
                orgUnit.getType().getName(),
                orgUnit.getParentUnit() != null ? orgUnit.getParentUnit().getName() : null,
                orgUnit.getAttributes(),
                userCount,
                courseCount,
                facultyCount,
                courseResponses
        );
    }

    private void collectAllDescendantsInMemory(UUID unitId, Map<UUID, List<OrgUnit>> parentToChildren, List<UUID> ids) {
        ids.add(unitId);
        List<OrgUnit> children = parentToChildren.get(unitId);
        if (children != null) {
            for (OrgUnit child : children) {
                collectAllDescendantsInMemory(child.getId(), parentToChildren, ids);
            }
        }
    }
}
