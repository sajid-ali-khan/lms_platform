package com.hilip.lms.controllers;

import com.hilip.lms.dtos.TenantCreateDto;
import com.hilip.lms.services.TenantService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenant")
@AllArgsConstructor
public class TenantController {
    private final TenantService tenantService;

    @PostMapping
    public ResponseEntity<?> createTenant(@RequestBody TenantCreateDto dto){
        return ResponseEntity.ok(tenantService.createTenant(dto));
    }

    @GetMapping("/list")
    public ResponseEntity<?> getAllTenants(){
        return ResponseEntity.ok(tenantService.getAllTenants());
    }

    @GetMapping("/structure")
    public ResponseEntity<?> getTenantStructure(@RequestParam String tenantId){
        return ResponseEntity.ok(tenantService.getTenantStructure(tenantId));
    }
}
