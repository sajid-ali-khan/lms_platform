package com.hilip.lms.services;

import com.hilip.lms.dtos.CreateUserRequest;
import com.hilip.lms.dtos.UserResponse;
import com.hilip.lms.exceptions.DataAlreadyExistsException;
import com.hilip.lms.exceptions.ResourceNotFoundException;
import com.hilip.lms.models.Tenant;
import com.hilip.lms.models.User;
import com.hilip.lms.models.UserRole;
import com.hilip.lms.repositories.TenantRepository;
import com.hilip.lms.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TenantRepository tenantRepository;

    public UserResponse createUser(CreateUserRequest request){
        Tenant tenant = tenantRepository.findById(UUID.fromString(request.tenantId()))
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + request.tenantId()));

        if (tenant.getAdmin() != null){
            throw new DataAlreadyExistsException("Tenant already has an admin user: " + tenant.getAdmin().getUsername());
        }
        if (userRepository.existsByUsername(request.username())){
            throw new DataAlreadyExistsException("Username already exists: " + request.username());
        }
        User newUser = new User();
        newUser.setUsername(request.username());
        newUser.setFullName(request.fullName());
        newUser.setEmail(request.email());
        newUser.setPasswordHash(passwordEncoder.encode(request.password()));
        newUser.setRole(UserRole.valueOf(request.role()));
        newUser.setTenant(tenant);

        userRepository.save(newUser);
        log.info("Created new user with id: {}", newUser.getId());

        return new UserResponse(
                newUser.getId().toString(),
                newUser.getUsername(),
                newUser.getFullName(),
                newUser.getEmail(),
                newUser.getRole().name()
        );
    }
}
