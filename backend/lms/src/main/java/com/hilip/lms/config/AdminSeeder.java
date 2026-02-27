package com.hilip.lms.config;

import com.hilip.lms.models.User;
import com.hilip.lms.models.enums.UserRole;
import com.hilip.lms.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@Slf4j
public class AdminSeeder implements ApplicationRunner {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    public AdminSeeder(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        String adminUName = adminEmail;
        String adminPwd = adminPassword;

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
        admin.setPasswordHash(passwordEncoder.encode(adminPwd));
        admin.setRole(UserRole.SUPER_ADMIN);
        userRepository.save(admin);
        log.info("Admin created successfully");
    }
}
