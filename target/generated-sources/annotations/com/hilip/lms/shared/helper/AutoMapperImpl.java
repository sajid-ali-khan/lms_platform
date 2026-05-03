package com.hilip.lms.shared.helper;

import com.hilip.lms.course.lesson.Lesson;
import com.hilip.lms.course.lesson.dto.LessonResponse;
import com.hilip.lms.organization.orgstructure.OrgStructure;
import com.hilip.lms.organization.orgstructure.dto.OrgStructureResponse;
import com.hilip.lms.organization.orgstructure.dto.OrgStructureResponseBasic;
import com.hilip.lms.organization.orgunit.OrgUnit;
import com.hilip.lms.organization.orgunit.dto.OrgUnitDto;
import com.hilip.lms.organization.orgunit.dto.OrgUnitResponse;
import com.hilip.lms.organization.orgunittype.OrgUnitType;
import com.hilip.lms.organization.orgunittype.dto.OrgUnitTypeResponse;
import com.hilip.lms.tenant.Tenant;
import com.hilip.lms.tenant.dto.TenantAndAdminResponse;
import com.hilip.lms.tenant.dto.TenantResponse;
import com.hilip.lms.user.User;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-03T13:50:38+0530",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Ubuntu)"
)
@Component
public class AutoMapperImpl implements AutoMapper {

    @Override
    public OrgUnitTypeResponse mapOrgUnitTypeToOrgUnitTypeResponse(OrgUnitType orgUnitType) {
        if ( orgUnitType == null ) {
            return null;
        }

        String parentName = null;
        String id = null;
        String name = null;
        Integer level = null;

        parentName = orgUnitTypeParentTypeName( orgUnitType );
        if ( orgUnitType.getId() != null ) {
            id = orgUnitType.getId().toString();
        }
        name = orgUnitType.getName();
        level = orgUnitType.getLevel();

        OrgUnitTypeResponse orgUnitTypeResponse = new OrgUnitTypeResponse( id, name, level, parentName );

        return orgUnitTypeResponse;
    }

    @Override
    public OrgStructureResponseBasic mapOrgStructureToOrgStructureResponseBasic(OrgStructure orgStructure) {
        if ( orgStructure == null ) {
            return null;
        }

        String id = null;
        String name = null;

        if ( orgStructure.getId() != null ) {
            id = orgStructure.getId().toString();
        }
        name = orgStructure.getName();

        List<String> structure = orgStructure.getOrgUnitTypes().stream().map(OrgUnitType::getName).toList();

        OrgStructureResponseBasic orgStructureResponseBasic = new OrgStructureResponseBasic( id, name, structure );

        return orgStructureResponseBasic;
    }

    @Override
    public OrgStructureResponse mapOrgStructureToOrgStructureResponse(OrgStructure orgStructure) {
        if ( orgStructure == null ) {
            return null;
        }

        List<OrgUnitTypeResponse> structure = null;
        String id = null;
        String name = null;

        structure = orgUnitTypeListToOrgUnitTypeResponseList( orgStructure.getOrgUnitTypes() );
        if ( orgStructure.getId() != null ) {
            id = orgStructure.getId().toString();
        }
        name = orgStructure.getName();

        OrgStructureResponse orgStructureResponse = new OrgStructureResponse( id, name, structure );

        return orgStructureResponse;
    }

    @Override
    public TenantResponse mapTenantToTenantResponse(Tenant tenant) {
        if ( tenant == null ) {
            return null;
        }

        String admin = null;
        String id = null;
        String name = null;
        String category = null;

        admin = tenantAdminFullName( tenant );
        if ( tenant.getId() != null ) {
            id = tenant.getId().toString();
        }
        name = tenant.getName();
        if ( tenant.getCategory() != null ) {
            category = tenant.getCategory().name();
        }

        TenantResponse tenantResponse = new TenantResponse( id, name, category, admin );

        return tenantResponse;
    }

    @Override
    public OrgUnitResponse mapOrgUnitToOrgUnitResponse(OrgUnit orgUnit) {
        if ( orgUnit == null ) {
            return null;
        }

        String id = null;
        String name = null;

        if ( orgUnit.getId() != null ) {
            id = orgUnit.getId().toString();
        }
        name = orgUnit.getName();

        OrgUnitResponse orgUnitResponse = new OrgUnitResponse( id, name );

        return orgUnitResponse;
    }

    @Override
    public TenantAndAdminResponse mapTenantToTenantAndAdminResponse(Tenant tenant, String adminPassword) {
        if ( tenant == null && adminPassword == null ) {
            return null;
        }

        String tenantName = null;
        String tenantCategory = null;
        String adminEmail = null;
        if ( tenant != null ) {
            tenantName = tenant.getName();
            if ( tenant.getCategory() != null ) {
                tenantCategory = tenant.getCategory().name();
            }
            adminEmail = tenantAdminEmail( tenant );
        }
        String adminPassword1 = null;
        adminPassword1 = adminPassword;

        TenantAndAdminResponse tenantAndAdminResponse = new TenantAndAdminResponse( tenantName, tenantCategory, adminEmail, adminPassword1 );

        return tenantAndAdminResponse;
    }

    @Override
    public OrgUnitDto mapOrgUnitToOrgUnitDto(OrgUnit orgUnit) {
        if ( orgUnit == null ) {
            return null;
        }

        UUID parentId = null;
        int level = 0;
        UUID id = null;
        String name = null;

        parentId = orgUnitParentUnitId( orgUnit );
        Integer level1 = orgUnitTypeLevel( orgUnit );
        if ( level1 != null ) {
            level = level1;
        }
        id = orgUnit.getId();
        name = orgUnit.getName();

        OrgUnitDto orgUnitDto = new OrgUnitDto( id, name, level, parentId );

        return orgUnitDto;
    }

    @Override
    public LessonResponse mapLessonToLessonResponse(Lesson lesson) {
        if ( lesson == null ) {
            return null;
        }

        String id = null;
        String title = null;
        String content = null;
        String type = null;
        Integer sequenceOrder = null;
        String resourceUrl = null;
        Boolean isPublished = null;

        if ( lesson.getId() != null ) {
            id = lesson.getId().toString();
        }
        title = lesson.getTitle();
        content = lesson.getContent();
        if ( lesson.getType() != null ) {
            type = lesson.getType().name();
        }
        sequenceOrder = lesson.getSequenceOrder();
        resourceUrl = lesson.getResourceUrl();
        isPublished = lesson.getIsPublished();

        LessonResponse lessonResponse = new LessonResponse( id, title, content, type, sequenceOrder, resourceUrl, isPublished );

        return lessonResponse;
    }

    private String orgUnitTypeParentTypeName(OrgUnitType orgUnitType) {
        OrgUnitType parentType = orgUnitType.getParentType();
        if ( parentType == null ) {
            return null;
        }
        return parentType.getName();
    }

    protected List<OrgUnitTypeResponse> orgUnitTypeListToOrgUnitTypeResponseList(List<OrgUnitType> list) {
        if ( list == null ) {
            return null;
        }

        List<OrgUnitTypeResponse> list1 = new ArrayList<OrgUnitTypeResponse>( list.size() );
        for ( OrgUnitType orgUnitType : list ) {
            list1.add( mapOrgUnitTypeToOrgUnitTypeResponse( orgUnitType ) );
        }

        return list1;
    }

    private String tenantAdminFullName(Tenant tenant) {
        User admin = tenant.getAdmin();
        if ( admin == null ) {
            return null;
        }
        return admin.getFullName();
    }

    private String tenantAdminEmail(Tenant tenant) {
        User admin = tenant.getAdmin();
        if ( admin == null ) {
            return null;
        }
        return admin.getEmail();
    }

    private UUID orgUnitParentUnitId(OrgUnit orgUnit) {
        OrgUnit parentUnit = orgUnit.getParentUnit();
        if ( parentUnit == null ) {
            return null;
        }
        return parentUnit.getId();
    }

    private Integer orgUnitTypeLevel(OrgUnit orgUnit) {
        OrgUnitType type = orgUnit.getType();
        if ( type == null ) {
            return null;
        }
        return type.getLevel();
    }
}
