package com.hilip.lms.controllers;

import com.hilip.lms.dtos.CreateUserRequest;
import com.hilip.lms.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
