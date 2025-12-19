package com.hilip.lms.controllers;

import com.hilip.lms.dtos.CreateUserRequest;
import com.hilip.lms.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenants/{tenantId}/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

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
}
