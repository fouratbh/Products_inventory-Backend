package com.app.services;

import com.app.dto.*;
import com.app.entities.User;
import com.app.exceptions.ResourceNotFoundException;
import com.app.mapper.UserMapper;
import com.app.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {
	
	private final UserRepository userRepository;
	private final UserMapper userMapper;

    /**
     * Récupère les informations de l'utilisateur actuellement connecté via Keycloak.
     * Très utile pour ton OrderService afin de lier une commande à un utilisateur local.
     */
    public UserDto getCurrentUser() {
        // On récupère le JWT fourni par l'intercepteur Angular
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Le "preferred_username" est le champ standard dans Keycloak
        String username = jwt.getClaimAsString("preferred_username");
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé en base locale : " + username));
        
        return userMapper.toDTO(user);
    }
    
    /**
     * Méthode optionnelle pour synchroniser un utilisateur Keycloak avec ta base locale
     * si tu en as besoin pour tes relations Hibernate (Order -> User).
     */
    public void syncUserWithIdp(String username, String email) {
        if (!userRepository.existsByUsername(username)) {
            User newUser = User.builder()
                    .username(username)
                    .email(email)
                    .enabled(true)
                    .build();
            userRepository.save(newUser);
            log.info("Utilisateur synchronisé depuis Keycloak : {}", username);
        }
    }
}