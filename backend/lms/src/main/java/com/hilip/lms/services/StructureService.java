package com.hilip.lms.services;

import com.hilip.lms.dtos.StructureCreateDto;
import com.hilip.lms.models.OrgUnitType;
import com.hilip.lms.models.Tenant;
import com.hilip.lms.repositories.OrgUnitTypeRepository;
import com.hilip.lms.repositories.TenantRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class StructureService {
    private final OrgUnitTypeRepository orgUnitTypeRepository;
    private final TenantRepository tenantRepository;

    @Transactional
    public boolean createStructure(StructureCreateDto structureCreateDto) {
        Tenant tenant = tenantRepository.findById(java.util.UUID.fromString(structureCreateDto.tenantId()))
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));

        List<String> hierarchyLevels = structureCreateDto.hierarchyLevels();
        OrgUnitType parentType = null;
        for (int i = 0; i < hierarchyLevels.size(); i++) {
            OrgUnitType currentType = new OrgUnitType();
            currentType.setName(hierarchyLevels.get(i));
            currentType.setLevel(i);
            currentType.setTenant(tenant);
            if (parentType != null) {
                currentType.setParentType(parentType);
            }
            parentType = orgUnitTypeRepository.save(currentType);
        }
        return true;
    }
}
