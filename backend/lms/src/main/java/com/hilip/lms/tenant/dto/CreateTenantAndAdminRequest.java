package com.hilip.lms.tenant.dto;

public record CreateTenantAndAdminRequest(
        String tenantName,
        String tenantCategory,
        String adminFullName,
        String adminEmail
) {
}
