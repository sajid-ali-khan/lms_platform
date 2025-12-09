package com.hilip.lms.services;

import com.hilip.lms.dtos.TenantCreateDto;
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

    public Tenant createTenant(TenantCreateDto dto) {
        Tenant tenant = new Tenant();
        tenant.setName(dto.name());
        tenant.setCategory(TenantCategory.valueOf(dto.category()));
        return tenantRepository.save(tenant);
    }

    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }
}
