package com.hilip.lms.helpers;

import com.hilip.lms.dtos.TenantCreateDto;
import com.hilip.lms.dtos.TenantResponse;
import com.hilip.lms.models.Tenant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DtoMapper {
    TenantResponse mapTenantToTenantResponse(Tenant tenant);
}
