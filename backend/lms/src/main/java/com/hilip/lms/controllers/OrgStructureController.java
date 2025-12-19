package com.hilip.lms.controllers;

import com.hilip.lms.dtos.orgUnitType.CreateOrgUnitTypeRequest;
import com.hilip.lms.services.OrgStructureService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenants/{tenantId}/org-structures")
@AllArgsConstructor
public class OrgStructureController {
    private final OrgStructureService orgStructureService;

    @PostMapping
    public ResponseEntity<?> createStructure(
            @PathVariable("tenantId") String tenantId,
            @RequestBody CreateOrgUnitTypeRequest request
            ){
        var orgStructure = orgStructureService.createStructure(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(orgStructure);
    }

    @GetMapping
    public ResponseEntity<?> getTenantStructures(@PathVariable("tenantId") String tenantId){
        return ResponseEntity.ok(orgStructureService.getTenantStructures(tenantId));
    }
}
