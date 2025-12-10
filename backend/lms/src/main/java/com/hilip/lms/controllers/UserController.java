package com.hilip.lms.controllers;

import com.hilip.lms.dtos.CreateUserRequest;
import com.hilip.lms.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(
            @RequestBody CreateUserRequest request
            ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/list")
    public ResponseEntity<?> getAllUsersOfTenant(
            @RequestParam String tenantId
    ) {
        return ResponseEntity.ok(userService.getAllUsersOfTenant(tenantId));
    }
}
