package com.hilip.lms.tenant.dto;

public record TenantResponse (
        String id,
        String name,
        String category,
        String admin
){
}
