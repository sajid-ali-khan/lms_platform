package com.hilip.lms.controllers;

import com.hilip.lms.dtos.StructureCreateDto;
import com.hilip.lms.services.StructureService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/structure")
@AllArgsConstructor
public class StructureController {
    private final StructureService structureService;
    @PostMapping
    public ResponseEntity<?> createStructure(
            @RequestBody StructureCreateDto structureCreateDto
            ){
        if (structureService.createStructure(structureCreateDto)){
            return ResponseEntity.ok("Structure created successfully");
        } else {
            return ResponseEntity.status(500).body("Failed to create structure");
        }
    }
}
