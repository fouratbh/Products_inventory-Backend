package com.app.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

	@Service
	@RequiredArgsConstructor
	public class CustomUserDetailsService implements UserDetailsService {
	    
	    private final com.app.repos.UserRepository userRepository;
	    
	    @Override
	    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	        com.app.entities.User user = userRepository.findByUsernameWithRoles(username)
	            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
	        
	        List<GrantedAuthority> authorities = user.getRoles().stream()
	            .map(role -> new SimpleGrantedAuthority(role.getName()))
	            .collect(Collectors.toList());
	        
	        return User.builder()
	            .username(user.getUsername())
	            .password(user.getPassword())
	            .authorities(authorities)
	            .accountExpired(false)
	            .accountLocked(!user.getEnabled())
	            .credentialsExpired(false)
	            .disabled(!user.getEnabled())
	            .build();
	    }
	}


