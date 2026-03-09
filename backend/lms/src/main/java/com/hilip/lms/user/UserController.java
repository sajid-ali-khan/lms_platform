package com.hilip.lms.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hilip.lms.user.dto.CreateUserRequest;
import com.hilip.lms.user.userorgunit.UserOrgUnitService;
import com.hilip.lms.user.userorgunit.dto.AssignOrgUnitRequest;

@RestController
@RequestMapping("/api/tenants/{tenantId}/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserOrgUnitService userOrgUnitService;

    @PostMapping
    public ResponseEntity<?> createUser(
        @PathVariable("tenantId") String tenantId,
        @RequestBody CreateUserRequest request
        ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(tenantId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @PathVariable("tenantId") String tenantId,
            @PathVariable String id
    ) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAllUsersOfTenant(
            @PathVariable("tenantId") String tenantId
    ) {
        return ResponseEntity.ok(userService.getAllUsersOfTenant(tenantId));
    }

    // ─── User Org Unit Assignments ───────────────────────────────────────────

    @PostMapping("/{userId}/org-units")
    public ResponseEntity<?> assignOrgUnit(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("userId") String userId,
            @RequestBody AssignOrgUnitRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userOrgUnitService.assignOrgUnit(userId, request));
    }

    @GetMapping("/{userId}/org-units")
    public ResponseEntity<?> getUserOrgUnits(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("userId") String userId
    ) {
        return ResponseEntity.ok(userOrgUnitService.getUserOrgUnits(userId));
    }

    @DeleteMapping("/{userId}/org-units/{orgUnitId}")
    public ResponseEntity<?> removeOrgUnit(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("userId") String userId,
            @PathVariable("orgUnitId") String orgUnitId
    ) {
        userOrgUnitService.removeOrgUnit(userId, orgUnitId);
        return ResponseEntity.noContent().build();
    }
}
