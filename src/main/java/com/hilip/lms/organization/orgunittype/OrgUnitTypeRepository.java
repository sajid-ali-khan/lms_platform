package com.hilip.lms.organization.orgunittype;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrgUnitTypeRepository extends JpaRepository<OrgUnitType, UUID> {
}
