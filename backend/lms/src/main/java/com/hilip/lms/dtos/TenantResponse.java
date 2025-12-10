package com.hilip.lms.dtos;

public record TenantResponse (
        String id,
        String name,
        String category,
        String admin
){
}
