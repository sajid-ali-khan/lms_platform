package com.hilip.lms.services;

import com.hilip.lms.dtos.CreateOrgUnitRequest;
import com.hilip.lms.exceptions.ResourceNotFoundException;
import com.hilip.lms.helper.AutoMapper;
import com.hilip.lms.models.OrgUnit;
import com.hilip.lms.models.Tenant;
import com.hilip.lms.repositories.OrgUnitRepository;
import com.hilip.lms.repositories.OrgUnitTypeRepository;
import com.hilip.lms.repositories.TenantRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class OrgUnitService {
    private final OrgUnitRepository orgUnitRepository;
    private final OrgUnitTypeRepository orgUnitTypeRepository;
    private final TenantRepository tenantRepository;
    private final AutoMapper autoMapper;

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

        var newOrgUnit = new OrgUnit();
        newOrgUnit.setName(request.name());
        newOrgUnit.setType(orgUnitType);
        newOrgUnit.setTenant(tenant);
        newOrgUnit.setAttributes(request.attributes());
        if (!parentNull){
            OrgUnit parentUnit = orgUnitRepository.findById(UUID.fromString(request.parentOrgUnitId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Parent org unit not found for id: " + request.parentOrgUnitId()));
            newOrgUnit.setParentUnit(parentUnit);
        }
        orgUnitRepository.save(newOrgUnit);
    }
}
