package com.hilip.lms.dtos.tenant;

public record TenantResponse (
        String id,
        String name,
        String category,
        String admin
){
}
