package com.hilip.lms.controllers;

import com.hilip.lms.dtos.CreateOrgUnitRequest;
import com.hilip.lms.services.OrgUnitService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
