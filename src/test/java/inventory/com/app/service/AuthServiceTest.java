package inventory.com.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.app.dto.LoginRequest;
import com.app.dto.LoginResponse;
import com.app.dto.RefreshTokenRequest;
import com.app.dto.RegisterRequest;
import com.app.dto.UserDto;
import com.app.entities.Role;
import com.app.entities.User;
import com.app.exceptions.ResourceNotFoundException;
import com.app.mapper.UserMapper;
import com.app.repos.RoleRepository;
import com.app.repos.UserRepository;
import com.app.security.JwtTokenProvider;
import com.app.services.AuthService;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
public class AuthServiceTest {
	
	@Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private RoleRepository roleRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtTokenProvider tokenProvider;
    
    @Mock
    private UserMapper userMapper;
    
    @InjectMocks
    private AuthService authService;

    private User testUser;
    private Role testRole;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        testRole = Role.builder()
            .id(1L)
            .name("ROLE_SALES")
            .build();

        testUser = User.builder()
            .id(1L)
            .username("testuser")
            .email("test@example.com")
            .password("encodedPassword")
            .firstName("Test")
            .lastName("User")
            .enabled(true)
            .roles(Set.of(testRole))
            .build();

        loginRequest = LoginRequest.builder()
            .username("testuser")
            .password("password123")
            .build();

        registerRequest = RegisterRequest.builder()
            .username("newuser")
            .email("new@example.com")
            .password("password123")
            .firstName("New")
            .lastName("User")
            .roles(Set.of("ROLE_SALES"))
            .build();
    }

    @Test
    @DisplayName("Should successfully login with valid credentials")
    void shouldLoginSuccessfully() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("access-token");
        when(tokenProvider.generateRefreshToken(authentication)).thenReturn("refresh-token");
        when(userRepository.findByUsernameWithRoles(loginRequest.getUsername()))
            .thenReturn(Optional.of(testUser));
        
        UserDto userDto = UserDto.builder()
            .id(testUser.getId())
            .username(testUser.getUsername())
            .email(testUser.getEmail())
            .build();
        when(userMapper.toDTO(testUser)).thenReturn(userDto);

        // When
        LoginResponse response = authService.login(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getType()).isEqualTo("Bearer");
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getUser().getUsername()).isEqualTo("testuser");
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider).generateToken(authentication);
        verify(tokenProvider).generateRefreshToken(authentication);
        verify(userRepository).findByUsernameWithRoles("testuser");
    }

    @Test
    @DisplayName("Should throw exception when login with invalid credentials")
    void shouldThrowExceptionWhenLoginWithInvalidCredentials() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When/Then
        assertThatThrownBy(() -> authService.login(loginRequest))
            .isInstanceOf(BadCredentialsException.class)
            .hasMessage("Invalid credentials");
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider, never()).generateToken(any());
    }

    @Test
    @DisplayName("Should throw exception when user not found after authentication")
    void shouldThrowExceptionWhenUserNotFoundAfterAuth() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("token");
        when(tokenProvider.generateRefreshToken(authentication)).thenReturn("refresh");
        when(userRepository.findByUsernameWithRoles(loginRequest.getUsername()))
            .thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> authService.login(loginRequest))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("User not found");
    }

    @Test
    @DisplayName("Should successfully register new user")
    void shouldRegisterNewUserSuccessfully() {
        // Given
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(roleRepository.findByName("ROLE_SALES")).thenReturn(Optional.of(testRole));
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        UserDto userDto = UserDto.builder()
            .id(testUser.getId())
            .username(testUser.getUsername())
            .email(testUser.getEmail())
            .build();
        when(userMapper.toDTO(any(User.class))).thenReturn(userDto);

        // When
        UserDto result = authService.register(registerRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        
        verify(userRepository).existsByUsername(registerRequest.getUsername());
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when registering with existing username")
    void shouldThrowExceptionWhenUsernameExists() {
        // Given
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> authService.register(registerRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Username is already taken");
        
        verify(userRepository).existsByUsername(registerRequest.getUsername());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when registering with existing email")
    void shouldThrowExceptionWhenEmailExists() {
        // Given
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> authService.register(registerRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Email is already in use");
        
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when role not found during registration")
    void shouldThrowExceptionWhenRoleNotFound() {
        // Given
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(roleRepository.findByName("ROLE_SALES")).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> authService.register(registerRequest))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Role not found");
        
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should successfully refresh token")
    void shouldRefreshTokenSuccessfully() {
        // Given
        String refreshToken = "valid-refresh-token";
        RefreshTokenRequest request = RefreshTokenRequest.builder()
            .refreshToken(refreshToken)
            .build();
        
        when(tokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(tokenProvider.getUsernameFromToken(refreshToken)).thenReturn("testuser");
        when(userRepository.findByUsernameWithRoles("testuser")).thenReturn(Optional.of(testUser));
        when(tokenProvider.generateToken(any(Authentication.class))).thenReturn("new-token");
        when(tokenProvider.generateRefreshToken(any(Authentication.class))).thenReturn("new-refresh-token");
        
        UserDto userDto = UserDto.builder()
            .id(testUser.getId())
            .username(testUser.getUsername())
            .build();
        when(userMapper.toDTO(testUser)).thenReturn(userDto);

        // When
        LoginResponse response = authService.refreshToken(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("new-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
        
        verify(tokenProvider).validateToken(refreshToken);
        verify(tokenProvider).getUsernameFromToken(refreshToken);
        verify(userRepository).findByUsernameWithRoles("testuser");
    }

    @Test
    @DisplayName("Should throw exception when refresh token is invalid")
    void shouldThrowExceptionWhenRefreshTokenInvalid() {
        // Given
        RefreshTokenRequest request = RefreshTokenRequest.builder()
            .refreshToken("invalid-token")
            .build();
        
        when(tokenProvider.validateToken("invalid-token")).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> authService.refreshToken(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid refresh token");
        
        verify(tokenProvider).validateToken("invalid-token");
        verify(tokenProvider, never()).getUsernameFromToken(anyString());
    }
}





