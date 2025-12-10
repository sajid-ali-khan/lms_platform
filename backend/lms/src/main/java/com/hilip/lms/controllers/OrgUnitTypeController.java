package com.hilip.lms.controllers;

import com.hilip.lms.dtos.CreateOrgUnitTypeRequest;
import com.hilip.lms.services.OrgUnitTypeService;
import com.hilip.lms.services.TenantService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/tenant/structure")
@AllArgsConstructor
public class OrgUnitTypeController {
    private final OrgUnitTypeService orgUnitTypeService;
    private final TenantService tenantService;
    @PostMapping
    public ResponseEntity<?> createStructure(
            @RequestBody CreateOrgUnitTypeRequest request
            ){
        var orgStructure = orgUnitTypeService.createStructure(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(orgStructure);
    }

    @GetMapping
    public ResponseEntity<?> getTenantStructure(@RequestParam String tenantId){
        return ResponseEntity.ok(orgUnitTypeService.getTenantStructure(tenantId));
    }
}
