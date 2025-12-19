package com.hilip.lms.controllers;

import com.hilip.lms.dtos.CreateOrgUnitTypeRequest;
import com.hilip.lms.services.OrgUnitTypeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenants/{tenantId}/org-unit-types")
@AllArgsConstructor
public class OrgUnitTypeController {
    private final OrgUnitTypeService orgUnitTypeService;

    @PostMapping
    public ResponseEntity<?> createStructure(
            @PathVariable("tenantId") String tenantId,
            @RequestBody CreateOrgUnitTypeRequest request
            ){
        var orgStructure = orgUnitTypeService.createStructure(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(orgStructure);
    }

    @GetMapping
    public ResponseEntity<?> getTenantStructures(@PathVariable("tenantId") String tenantId){
        return ResponseEntity.ok(orgUnitTypeService.getTenantStructures(tenantId));
    }
}
