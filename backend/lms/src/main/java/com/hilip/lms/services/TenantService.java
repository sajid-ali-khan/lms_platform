package com.hilip.lms.services;

import com.hilip.lms.dtos.TenantCreateDto;
import com.hilip.lms.dtos.TenantResponse;
import com.hilip.lms.exceptions.DataAlreadyExistsException;
import com.hilip.lms.helper.AutoMapper;
import com.hilip.lms.models.Tenant;
import com.hilip.lms.models.TenantCategory;
import com.hilip.lms.repositories.TenantRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TenantService {
    private final TenantRepository tenantRepository;
    private final AutoMapper autoMapper;

    public TenantResponse createTenant(TenantCreateDto dto) {
        if (tenantRepository.existsByNameIgnoreCase(dto.name())) {
            throw new DataAlreadyExistsException("Tenant with name " + dto.name() + " already exists");
        }
        Tenant tenant = new Tenant();
        tenant.setName(dto.name());
        tenant.setCategory(TenantCategory.valueOf(dto.category()));
        return autoMapper.mapTenantToTenantResponse(tenantRepository.save(tenant));
    }

    public List<TenantResponse> getAllTenants() {
        return tenantRepository.findAll().stream().map(autoMapper::mapTenantToTenantResponse).toList();
    }


}
