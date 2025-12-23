package com.hilip.lms.services;

import com.hilip.lms.dtos.orgStructures.OrgStructureResponseBasic;
import com.hilip.lms.dtos.orgUnitType.CreateOrgUnitTypeRequest;
import com.hilip.lms.dtos.orgStructures.OrgStructureResponse;
import com.hilip.lms.exceptions.DataAlreadyExistsException;
import com.hilip.lms.exceptions.ResourceNotFoundException;
import com.hilip.lms.helper.AutoMapper;
import com.hilip.lms.models.OrgUnitType;
import com.hilip.lms.models.Tenant;
import com.hilip.lms.repositories.OrgUnitTypeRepository;
import com.hilip.lms.repositories.OrgStructureRepository;
import com.hilip.lms.repositories.TenantRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrgStructureService {
    private final OrgUnitTypeRepository orgUnitTypeRepository;
    private final TenantRepository tenantRepository;
    private final OrgStructureRepository orgStructureRepository;
    private final AutoMapper autoMapper;

    @Transactional
    public void createStructure(String tenantId, CreateOrgUnitTypeRequest request) {
        Tenant tenant = tenantRepository.findById(java.util.UUID.fromString(tenantId))
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));

        if (orgStructureRepository.existsByNameAndTenant(request.name(), tenant)) {
            throw new DataAlreadyExistsException("A organization structure of name " + request.name() + " already exists in the tenant " + tenant.getName());
        }
        com.hilip.lms.models.OrgStructure orgStructure = new com.hilip.lms.models.OrgStructure();
        orgStructure.setName(request.name());
        orgStructure.setTenant(tenant);
        orgStructure = orgStructureRepository.save(orgStructure);

        List<String> hierarchyLevels = request.hierarchyLevels();
        OrgUnitType parentType = null;
        for (int i = 0; i < hierarchyLevels.size(); i++) {
            OrgUnitType currentType = new OrgUnitType();
            currentType.setName(hierarchyLevels.get(i));
            currentType.setLevel(i);
            currentType.setOrgStructure(orgStructure);
            if (parentType != null) {
                currentType.setParentType(parentType);
            }
            parentType = orgUnitTypeRepository.save(currentType);
        }
    }

    public List<OrgStructureResponse> getTenantStructures(String tenantId) {
        Tenant tenant = tenantRepository.findById(UUID.fromString(tenantId))
                            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found."));

        return tenant.getOrgStructures().stream()
                    .map(autoMapper::mapOrgStructureToOrgStructureResponse)
                    .toList();
    }

    public List<OrgStructureResponseBasic> getTenantStructuresBasicInfo(String tenantId) {
        Tenant tenant = tenantRepository.findById(UUID.fromString(tenantId))
                            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found."));

        return tenant.getOrgStructures().stream()
                .map(autoMapper::mapOrgStructureToOrgStructureResponseBasic)
                .toList();
    }



}
