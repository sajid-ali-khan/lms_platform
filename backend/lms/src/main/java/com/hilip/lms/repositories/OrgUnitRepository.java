package com.hilip.lms.repositories;

import com.hilip.lms.models.OrgUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrgUnitRepository extends JpaRepository<OrgUnit, UUID> {
}
