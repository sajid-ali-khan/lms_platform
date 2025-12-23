package com.hilip.lms.controllers;

import com.hilip.lms.dtos.orgUnit.CreateOrgUnitRequest;
import com.hilip.lms.dtos.orgUnit.OrgUnitResponse;
import com.hilip.lms.services.OrgUnitService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants/{tenantId}/org-units")
@AllArgsConstructor
@Slf4j
public class OrgUnitController {
    private final OrgUnitService orgUnitService;

    @PostMapping
    public ResponseEntity<?> createOrgUnit(
            @PathVariable("tenantId") String tenantId,
            @RequestBody CreateOrgUnitRequest request
            ) {
        log.info("OrgUnitDto: {}", request);
        orgUnitService.createOrgUnit(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<?> getOrgUnits(
            @PathVariable("tenantId") String tenantId,
            @RequestParam String structureName,
            @RequestParam String typeName,
            @RequestParam(required = false) String parentUnitId
    ) {
        log.debug("Fetching org units for tenantId: {}, structureName: {}, typeName: {}", tenantId, structureName, typeName);
        List<OrgUnitResponse> response;
        if (parentUnitId == null) {
            response = orgUnitService.getOrgUnitsByTenantAndStructureAndType(tenantId, structureName, typeName);
        }else {
            response = orgUnitService.getOrgUnitsByTenantStructureTypeAndParentUnit(tenantId, structureName, typeName, parentUnitId);
        }
        return ResponseEntity.ok(response);

    }

    @GetMapping("/structure/{structureName}/tree")
    public ResponseEntity<?> getOrgUnitsByTenantAndStructure(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("structureName") String structureName
    ){
        return ResponseEntity.ok(
                orgUnitService.getOrgUnitsTreeByTenantAndStructure(tenantId, structureName)
        );
    }
}
