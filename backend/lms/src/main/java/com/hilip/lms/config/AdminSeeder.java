package com.hilip.lms.config;

import com.hilip.lms.models.User;
import com.hilip.lms.models.enums.UserRole;
import com.hilip.lms.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@Slf4j
@AllArgsConstructor
public class AdminSeeder implements ApplicationRunner {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        String adminUName="admin@lms";
        String adminPassword = "admin@1584";

        if (args.getOptionValues("seed") == null){
            log.info("Admin seeder skipped.");
            return;
        }

        if (userRepository.existsByEmail(adminUName)){
            log.info("Admin already exists.");
            return;
        }

        User admin = new User();
        admin.setEmail(adminUName);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setRole(UserRole.SUPER_ADMIN);
        userRepository.save(admin);
        log.info("Admin created successfully");
    }
}
