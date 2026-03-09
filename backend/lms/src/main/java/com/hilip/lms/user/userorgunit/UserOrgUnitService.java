package com.hilip.lms.user.userorgunit;

import com.hilip.lms.organization.orgunit.OrgUnit;
import com.hilip.lms.organization.orgunit.OrgUnitRepository;
import com.hilip.lms.shared.exceptions.DataAlreadyExistsException;
import com.hilip.lms.shared.exceptions.ResourceNotFoundException;
import com.hilip.lms.user.User;
import com.hilip.lms.user.UserRepository;
import com.hilip.lms.user.userorgunit.dto.AssignOrgUnitRequest;
import com.hilip.lms.user.userorgunit.dto.UserOrgUnitResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class UserOrgUnitService {
    private final UserOrgUnitRepository userOrgUnitRepository;
    private final UserRepository userRepository;
    private final OrgUnitRepository orgUnitRepository;

    public UserOrgUnitResponse assignOrgUnit(String userId, AssignOrgUnitRequest request) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        OrgUnit orgUnit = orgUnitRepository.findById(UUID.fromString(request.orgUnitId()))
                .orElseThrow(() -> new ResourceNotFoundException("Org unit not found with id: " + request.orgUnitId()));

        if (!user.getTenant().getId().equals(orgUnit.getTenant().getId())) {
            throw new IllegalArgumentException("User and org unit belong to different tenants");
        }

        if (userOrgUnitRepository.existsByUserIdAndOrgUnitId(user.getId(), orgUnit.getId())) {
            throw new DataAlreadyExistsException("User is already assigned to this org unit");
        }

        UserOrgUnit userOrgUnit = new UserOrgUnit();
        userOrgUnit.setUser(user);
        userOrgUnit.setOrgUnit(orgUnit);
        userOrgUnit.setPrimary(request.isPrimary());

        userOrgUnit = userOrgUnitRepository.save(userOrgUnit);
        log.info("Assigned user {} to org unit {}", userId, request.orgUnitId());

        return mapToResponse(userOrgUnit);
    }

    public List<UserOrgUnitResponse> getUserOrgUnits(String userId) {
        userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return userOrgUnitRepository.findAllByUserId(UUID.fromString(userId))
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void removeOrgUnit(String userId, String orgUnitId) {
        UUID uid = UUID.fromString(userId);
        UUID ouid = UUID.fromString(orgUnitId);

        if (!userOrgUnitRepository.existsByUserIdAndOrgUnitId(uid, ouid)) {
            throw new ResourceNotFoundException("User is not assigned to this org unit");
        }

        userOrgUnitRepository.deleteByUserIdAndOrgUnitId(uid, ouid);
        log.info("Removed user {} from org unit {}", userId, orgUnitId);
    }

    private UserOrgUnitResponse mapToResponse(UserOrgUnit uou) {
        return new UserOrgUnitResponse(
                uou.getId().toString(),
                uou.getOrgUnit().getId().toString(),
                uou.getOrgUnit().getName(),
                buildOrgUnitPath(uou.getOrgUnit()),
                uou.isPrimary()
        );
    }

    private String buildOrgUnitPath(OrgUnit orgUnit) {
        List<String> parts = new ArrayList<>();
        OrgUnit current = orgUnit;
        while (current != null) {
            parts.add(current.getName());
            current = current.getParentUnit();
        }
        Collections.reverse(parts);
        return String.join(" / ", parts);
    }
}
