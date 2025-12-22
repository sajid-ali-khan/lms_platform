package com.hilip.lms.dtos.tenant;

public record TenantAndAdminResponse(
        String tenantName,
        String tenantCategory,
        String adminEmail,
        String adminPassword
) {
}
