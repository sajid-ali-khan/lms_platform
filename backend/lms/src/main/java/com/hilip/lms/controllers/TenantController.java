package com.hilip.lms.controllers;

import com.hilip.lms.dtos.tenant.CreateTenantAndAdminRequest;
import com.hilip.lms.services.TenantService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenants")
@AllArgsConstructor
public class TenantController {
    private final TenantService tenantService;

    @PostMapping
    public ResponseEntity<?> createTenant(@RequestBody CreateTenantAndAdminRequest dto){
        return ResponseEntity.ok(tenantService.createTenantWithAdmin(dto));
    }

    @GetMapping
    public ResponseEntity<?> getAllTenants(){
        return ResponseEntity.ok(tenantService.getAllTenants());
    }
}
