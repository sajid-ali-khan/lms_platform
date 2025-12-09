package com.hilip.lms.controllers;

import com.hilip.lms.dtos.CreateOrgUnitTypeRequest;
import com.hilip.lms.services.OrgUnitTypeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/structure")
@AllArgsConstructor
public class OrgUnitTypeController {
    private final OrgUnitTypeService orgUnitTypeService;
    @PostMapping
    public ResponseEntity<?> createStructure(
            @RequestBody CreateOrgUnitTypeRequest request
            ){
        if (orgUnitTypeService.createStructure(request)){
            return ResponseEntity.ok("Structure created successfully");
        } else {
            return ResponseEntity.status(500).body("Failed to create structure");
        }
    }
}
