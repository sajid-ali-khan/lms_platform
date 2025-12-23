package com.hilip.lms.controllers;

import com.hilip.lms.services.DashboardService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tenants/{tenantId}/admin/dashboard")
@AllArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<?> getDashboardDetails(@PathVariable("tenantId") String tenantId){
        return ResponseEntity.ok(dashboardService.getDashboardDetails(tenantId));
    }
}
