package com.hilip.lms.organization.orgstructure;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hilip.lms.organization.orgunittype.dto.CreateOrgUnitTypeRequest;

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
        orgStructureService.createStructure(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/detailed")
    public ResponseEntity<?> getDetailedTenantStructures(@PathVariable("tenantId") String tenantId){
        return ResponseEntity.ok(orgStructureService.getTenantStructures(tenantId));
    }

    @GetMapping
    public ResponseEntity<?> getTenantStructures(@PathVariable("tenantId") String tenantId){
        return ResponseEntity.ok(orgStructureService.getTenantStructuresBasicInfo(tenantId));
    }
}
