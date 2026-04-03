package com.app.services;

import com.app.dto.*;
import com.app.entities.Role;
import com.app.entities.User;
import com.app.exceptions.ResourceNotFoundException;
import com.app.mapper.UserMapper;
import com.app.repos.RoleRepository;
import com.app.repos.UserRepository;
import com.app.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {
	
	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider tokenProvider;
	private final UserMapper userMapper;
	
	public LoginResponse login(LoginRequest request) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
				request.getUsername(), request.getPassword()));
		
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String token = tokenProvider.generateToken(authentication);
		String refreshToken = tokenProvider.generateRefreshToken(authentication);
		
		User user = userRepository.findByUsernameWithRoles(request.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		
		log.info("User {} logged in successfully", request.getUsername());
		
		return LoginResponse.builder()
				.token(token)
				.refreshToken(refreshToken)
				.type("Bearer")
				.user(userMapper.toDTO(user))
				.build();
		
	}
	
	
	
	
	public UserDto register(RegisterRequest request) {
		
		if(userRepository.existsByUsername(request.getUsername())) {
			throw new IllegalArgumentException("Username is already taken");
		}
		
		if(userRepository.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("Email is already in use");
		}
		
		Set<Role> roles = request.getRoles().stream()
				.map(roleName -> roleRepository.findByName(roleName)
						.orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName)))
				.collect(Collectors.toSet());
		
		
		User user = User.builder()
				.username(request.getUsername())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.enabled(true)
				.roles(roles)
				.build(); 
		
		
		User savedUser = userRepository.save(user);
		
		log.info("User {} registered successfully", request.getUsername());
		
		return userMapper.toDTO(savedUser);
	}
	
	
	
	public LoginResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        
        String username = tokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsernameWithRoles(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            username, null, 
            user.getRoles().stream()
                .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList())
        );
        
        String newToken = tokenProvider.generateToken(authentication);
        String newRefreshToken = tokenProvider.generateRefreshToken(authentication);
        
        return LoginResponse.builder()
            .token(newToken)
            .refreshToken(newRefreshToken)
            .type("Bearer")
            .user(userMapper.toDTO(user))
            .build();
    }

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
