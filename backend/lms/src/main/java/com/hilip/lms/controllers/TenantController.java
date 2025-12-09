package com.hilip.lms.controllers;

import com.hilip.lms.dtos.TenantCreateDto;
import com.hilip.lms.helpers.DtoMapper;
import com.hilip.lms.services.TenantService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tenants")
@AllArgsConstructor
public class TenantController {
    private final TenantService tenantService;
    private final DtoMapper dtoMapper;

    @PostMapping
    public ResponseEntity<?> createTenant(@RequestBody TenantCreateDto dto){
        return ResponseEntity.ok(tenantService.createTenant(dto));
    }
}
