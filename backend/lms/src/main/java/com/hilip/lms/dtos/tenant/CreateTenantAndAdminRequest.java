package com.hilip.lms.dtos.tenant;

public record CreateTenantAndAdminRequest(
        String tenantName,
        String tenantCategory,
        String adminFullName,
        String adminEmail
) {
}
