package com.hilip.lms.services;

import com.hilip.lms.dtos.user.CreateUserRequest;
import com.hilip.lms.dtos.user.UserResponse;
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

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TenantRepository tenantRepository;

    public UserResponse createUser(String tenantId, CreateUserRequest request){
        Tenant tenant = tenantRepository.findById(UUID.fromString(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + tenantId));

        if (request.role().equals("ADMIN") && tenant.getAdmin() != null){
            throw new DataAlreadyExistsException("Tenant already has an admin user: " + tenant.getAdmin().getUsername());
        }
        if (userRepository.existsByEmailAndTenant(request.email(), tenant)){
            throw new DataAlreadyExistsException("Email already exists: " + request.email() + " in the tenant " + tenant.getName());
        }
        User newUser = new User();
        newUser.setFullName(request.fullName());
        newUser.setEmail(request.email());
        newUser.setPasswordHash(passwordEncoder.encode(request.password()));
        newUser.setRole(UserRole.valueOf(request.role()));
        newUser.setTenant(tenant);

        userRepository.save(newUser);

        if (newUser.getRole() == UserRole.ADMIN){
            tenant.setAdmin(newUser);
            tenantRepository.save(tenant);
        }
        log.info("Created new user with id: {}", newUser.getId());

        return new UserResponse(
                newUser.getId().toString(),
                newUser.getUsername(),
                newUser.getFullName(),
                newUser.getEmail(),
                newUser.getRole().name()
        );
    }

    public UserResponse getUserById(String id){
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return new UserResponse(
                user.getId().toString(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    public Iterable<UserResponse> getAllUsersOfTenant(String tenantId){
        Tenant tenant = tenantRepository.findById(UUID.fromString(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + tenantId));
        List<User> users = userRepository.findAllByTenant(tenant);
        return users.stream().map(user -> new UserResponse(
                user.getId().toString(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name()
        )).toList();
    }
}
