package com.hilip.lms.tenant.dto;

public record TenantAndAdminResponse(
        String tenantName,
        String tenantCategory,
        String adminEmail,
        String adminPassword
) {
}
