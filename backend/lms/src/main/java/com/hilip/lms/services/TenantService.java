package com.hilip.lms.services;

import com.hilip.lms.dtos.tenant.CreateTenantAndAdminRequest;
import com.hilip.lms.dtos.tenant.TenantAndAdminResponse;
import com.hilip.lms.dtos.tenant.TenantCreateDto;
import com.hilip.lms.dtos.tenant.TenantResponse;
import com.hilip.lms.exceptions.DataAlreadyExistsException;
import com.hilip.lms.helper.AutoMapper;
import com.hilip.lms.models.Tenant;
import com.hilip.lms.models.enums.TenantCategory;
import com.hilip.lms.models.User;
import com.hilip.lms.models.enums.UserRole;
import com.hilip.lms.repositories.TenantRepository;
import com.hilip.lms.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class TenantService {
    private final TenantRepository tenantRepository;
    private final AutoMapper autoMapper;
    private final RandomPasswordGenerator randomPasswordGenerator;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

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

    @Transactional
    public TenantAndAdminResponse createTenantWithAdmin(CreateTenantAndAdminRequest dto) {
        log.debug("Creating tenant with name: {}", dto.tenantName());
        log.debug("Dto details: {}", dto);
        if (tenantRepository.existsByNameIgnoreCase(dto.tenantName())) {
            throw new DataAlreadyExistsException("Tenant with name " + dto.tenantName() + " already exists");
        }

        if (userRepository.existsByEmail(dto.adminEmail())) {
            throw new DataAlreadyExistsException("User with email " + dto.adminEmail() + " already exists");
        }
        User admin = new User();
        admin.setFullName(dto.adminFullName());
        admin.setEmail(dto.adminEmail());
        admin.setRole(UserRole.ADMIN);
        String adminPassword = randomPasswordGenerator.generateRandomPassword(12);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin = userRepository.save(admin);

        log.debug("Admin user created with email: {}", admin.getEmail());

        Tenant tenant = new Tenant();
        tenant.setName(dto.tenantName());
        tenant.setAdmin(admin);
        tenant.setCategory(TenantCategory.valueOf(dto.tenantCategory()));

        tenant = tenantRepository.save(tenant);
        admin.setTenant(tenant);
        userRepository.save(admin);

        log.debug("Tenant created with name: {}", tenant.getName());
        return autoMapper.mapTenantToTenantAndAdminResponse(tenant, adminPassword);
    }


}
