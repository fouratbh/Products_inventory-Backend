package inventory.com.app.service;



import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import com.app.dto.UserDto;
import com.app.entities.User;
import com.app.exceptions.ResourceNotFoundException;
import com.app.mapper.UserMapper;
import com.app.repos.UserRepository;
import com.app.services.AuthService;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests (Keycloak Integration)")
public class AuthServiceTest {
	
	private static final Logger log = LoggerFactory.getLogger(AuthServiceTest.class);

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1L)
            .username("testuser")
            .email("test@example.com")
            .enabled(true)
            .build();

        testUserDto = UserDto.builder()
            .id(1L)
            .username("testuser")
            .email("test@example.com")
            .build();
    }

    @Test
    @DisplayName("Should successfully get current user from JWT context")
    void shouldGetCurrentUserFromJwt() {
        // Given: Simuler le contexte de sécurité Spring avec un JWT Keycloak
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("preferred_username")).thenReturn("testuser");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userMapper.toDTO(testUser)).thenReturn(testUserDto);

        // When
        UserDto result = authService.getCurrentUser();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should throw exception when current user in JWT is not in local database")
    void shouldThrowExceptionWhenUserNotInDatabase() {
        // Given
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("preferred_username")).thenReturn("unknown");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> authService.getCurrentUser())
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Utilisateur non trouvé");
    }

    @Test
    @DisplayName("Should sync user when it does not exist locally")
    void shouldSyncUserWhenNotExists() {
        // Given
        String username = "newkeycloakuser";
        String email = "new@keycloak.com";
        when(userRepository.existsByUsername(username)).thenReturn(false);

        // When
        authService.syncUserWithIdp(username, email);

        // Then
        verify(userRepository).save(any(User.class));
        log.info("Vérification de la sauvegarde d'un nouvel utilisateur synchronisé");
    }

    @Test
    @DisplayName("Should not save user if it already exists during sync")
    void shouldNotSaveIfUserExistsDuringSync() {
        // Given
        String username = "testuser";
        when(userRepository.existsByUsername(username)).thenReturn(true);

        // When
        authService.syncUserWithIdp(username, "test@example.com");

        // Then
        verify(userRepository, never()).save(any(User.class));
    }
}




