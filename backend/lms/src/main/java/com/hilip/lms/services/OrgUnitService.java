package com.hilip.lms.services;

import com.hilip.lms.dtos.orgUnit.CreateOrgUnitRequest;
import com.hilip.lms.dtos.orgUnit.OrgUnitDetails;
import com.hilip.lms.dtos.orgUnit.OrgUnitDto;
import com.hilip.lms.dtos.orgUnit.OrgUnitResponse;
import com.hilip.lms.exceptions.ResourceNotFoundException;
import com.hilip.lms.helper.AutoMapper;
import com.hilip.lms.models.OrgStructure;
import com.hilip.lms.models.OrgUnit;
import com.hilip.lms.models.Tenant;
import com.hilip.lms.repositories.OrgStructureRepository;
import com.hilip.lms.repositories.OrgUnitRepository;
import com.hilip.lms.repositories.OrgUnitTypeRepository;
import com.hilip.lms.repositories.TenantRepository;
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

        return autoMapper.mapOrgUnitToOrgUnitDetails(orgUnit);
    }
}
