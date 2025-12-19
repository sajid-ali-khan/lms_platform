package com.hilip.lms.services;

import com.hilip.lms.dtos.orgUnitType.CreateOrgUnitTypeRequest;
import com.hilip.lms.dtos.orgUnitType.OrgUnitTypeResponse;
import com.hilip.lms.dtos.OrgStructureResponse;
import com.hilip.lms.exceptions.DataAlreadyExistsException;
import com.hilip.lms.exceptions.ResourceNotFoundException;
import com.hilip.lms.helper.AutoMapper;
import com.hilip.lms.models.OrgUnitType;
import com.hilip.lms.models.Tenant;
import com.hilip.lms.repositories.OrgUnitTypeRepository;
import com.hilip.lms.repositories.TenantOrgUnitTypeRepository;
import com.hilip.lms.repositories.TenantRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrgUnitTypeService {
    private final OrgUnitTypeRepository orgUnitTypeRepository;
    private final TenantRepository tenantRepository;
    private final TenantOrgUnitTypeRepository tenantOrgUnitTypeRepository;
    private final AutoMapper autoMapper;

    @Transactional
    public List<OrgUnitTypeResponse> createStructure(String tenantId, CreateOrgUnitTypeRequest request) {
        Tenant tenant = tenantRepository.findById(java.util.UUID.fromString(tenantId))
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));

        if (tenantOrgUnitTypeRepository.existsByNameAndTenant(request.name(), tenant)) {
            throw new DataAlreadyExistsException("A organization structure of name " + request.name() + " already exists in the tenant " + tenant.getName());
        }
        com.hilip.lms.models.OrgStructure orgStructure = new com.hilip.lms.models.OrgStructure();
        orgStructure.setName(request.name());
        orgStructure.setTenant(tenant);
        orgStructure = tenantOrgUnitTypeRepository.save(orgStructure);

        List<String> hierarchyLevels = request.hierarchyLevels();
        List<OrgUnitTypeResponse> response = new ArrayList<>();
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
            response.add(autoMapper.mapOrgUnitTypeToOrgUnitTypeResponse(parentType));
        }
        return response;
    }

    public List<OrgStructureResponse> getTenantStructures(String tenantId) {
        Tenant tenant = tenantRepository.findById(UUID.fromString(tenantId))
                            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found."));

        return tenant.getOrgStructures().stream()
                    .map(autoMapper::mapOrgStructureToOrgStructureResponse)
                    .toList();
    }
}
