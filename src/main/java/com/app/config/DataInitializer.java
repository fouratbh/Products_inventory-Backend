package com.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;

import com.app.entities.Role;
import com.app.entities.User;
import com.app.repos.RoleRepository;
import com.app.repos.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j

public class DataInitializer {
	
	@Value("${app.default.admin.password}")
	private String adminPassword;
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    
    
    @Bean
    public CommandLineRunner initData() {
    	
    	
        return args -> {
            // Créer les rôles s'ils n'existent pas
            createRoleIfNotExists("ROLE_ADMIN", "Administrateur système");
            createRoleIfNotExists("ROLE_MANAGER", "Gestionnaire");
            createRoleIfNotExists("ROLE_SALES", "Commercial");
            createRoleIfNotExists("ROLE_WAREHOUSE", "Magasinier");
            
            // Créer un utilisateur admin par défaut
            if (!userRepository.existsByUsername("admin")) {
                Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));
                
                User admin = User.builder()
                    .username("admin")
                    .email("admin@inventory.com")
                    .password(passwordEncoder.encode(adminPassword))
                    .firstName("Admin")
                    .lastName("System")
                    .enabled(true)
                    .roles(Set.of(adminRole))
                    .build();
                
                userRepository.save(admin);
                log.info("Admin user created - username: admin, password: admin123");
            }
            
            log.info("Data initialization completed");
        };
    }
    
    private void createRoleIfNotExists(String roleName, String description) {
        if (!roleRepository.findByName(roleName).isPresent()) {
            Role role = Role.builder()
                .name(roleName)
                .description(description)
                .build();
            roleRepository.save(role);
            log.info("Role created: {}", roleName);
        }
    }
}