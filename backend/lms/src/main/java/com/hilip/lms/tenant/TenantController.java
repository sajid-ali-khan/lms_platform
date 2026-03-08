package com.hilip.lms.tenant;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hilip.lms.tenant.dto.CreateTenantAndAdminRequest;
import com.hilip.lms.tenant.service.DashboardService;
import com.hilip.lms.tenant.service.TenantService;

@RestController
@RequestMapping("/api/tenants")
@AllArgsConstructor
public class TenantController {
    private final TenantService tenantService;
    private final DashboardService dashboardService;

    @PostMapping
    public ResponseEntity<?> createTenant(@RequestBody CreateTenantAndAdminRequest dto){
        return ResponseEntity.ok(tenantService.createTenantWithAdmin(dto));
    }

    @GetMapping
    public ResponseEntity<?> getAllTenants(){
        return ResponseEntity.ok(tenantService.getAllTenants());
    }

    @GetMapping("/{tenantId}/admin/dashboard")
    public ResponseEntity<?> getDashboardDetails(@PathVariable("tenantId") String tenantId){
        return ResponseEntity.ok(dashboardService.getDashboardDetails(tenantId));
    }
}
