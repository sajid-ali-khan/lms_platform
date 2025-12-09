package com.hilip.lms.services;

import com.hilip.lms.dtos.TenantCreateDto;
import com.hilip.lms.dtos.TenantResponse;
import com.hilip.lms.helpers.DtoMapper;
import com.hilip.lms.models.Tenant;
import com.hilip.lms.models.TenantCategory;
import com.hilip.lms.repositories.TenantRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TenantService {
    private final TenantRepository tenantRepository;
    private final DtoMapper dtoMapper;

    public TenantResponse createTenant(TenantCreateDto dto) {
        Tenant tenant = new Tenant();
        tenant.setName(dto.name());
        tenant.setCategory(TenantCategory.valueOf(dto.category()));
        return dtoMapper.mapTenantToTenantResponse(tenantRepository.save(tenant));
    }
}
