package inventory.com.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.app.dto.PasswordChangeDto;
import com.app.dto.UserCreateDto;
import com.app.dto.UserDto;
import com.app.dto.UserUpdateDto;
import com.app.entities.Role;
import com.app.entities.User;
import com.app.exceptions.ResourceNotFoundException;
import com.app.mapper.UserMapper;
import com.app.repos.RoleRepository;
import com.app.repos.UserRepository;
import com.app.services.UserService;
	
	@ExtendWith(MockitoExtension.class)
	@DisplayName("UserService Tests")
	public class UserServiceTest {

	    @Mock
	    private UserRepository userRepository;
	    
	    @Mock
	    private RoleRepository roleRepository;
	    
	    @Mock
	    private PasswordEncoder passwordEncoder;
	    
	    @Mock
	    private UserMapper userMapper;
	    
	    @InjectMocks
	    private UserService userService;

	    private User testUser;
	    private Role testRole;
	    private UserDto UserDto;

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

	        UserDto = UserDto.builder()
	            .id(1L)
	            .username("testuser")
	            .email("test@example.com")
	            .firstName("Test")
	            .lastName("User")
	            .enabled(true)
	            .build();
	    }

	    @Test
	    @DisplayName("Should get all users with pagination")
	    void shouldGetAllUsersWithPagination() {
	        // Given
	        Pageable pageable = PageRequest.of(0, 10);
	        Page<User> userPage = new PageImpl<>(List.of(testUser));
	        when(userRepository.findAll(pageable)).thenReturn(userPage);
	        when(userMapper.toDTO(testUser)).thenReturn(UserDto);

	        // When
	        Page<UserDto> result = userService.getAllUsers(pageable);

	        // Then
	        assertThat(result).isNotNull();
	        assertThat(result.getContent()).hasSize(1);
	        assertThat(result.getContent().get(0).getUsername()).isEqualTo("testuser");
	        
	        verify(userRepository).findAll(pageable);
	        verify(userMapper).toDTO(testUser);
	    }

	    @Test
	    @DisplayName("Should get user by id")
	    void shouldGetUserById() {
	        // Given
	        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
	        when(userMapper.toDTO(testUser)).thenReturn(UserDto);

	        // When
	        UserDto result = userService.getUserById(1L);

	        // Then
	        assertThat(result).isNotNull();
	        assertThat(result.getId()).isEqualTo(1L);
	        assertThat(result.getUsername()).isEqualTo("testuser");
	        
	        verify(userRepository).findById(1L);
	    }

	    @Test
	    @DisplayName("Should throw exception when user not found by id")
	    void shouldThrowExceptionWhenUserNotFoundById() {
	        // Given
	        when(userRepository.findById(999L)).thenReturn(Optional.empty());

	        // When/Then
	        assertThatThrownBy(() -> userService.getUserById(999L))
	            .isInstanceOf(ResourceNotFoundException.class)
	            .hasMessageContaining("User not found with id: 999");
	        
	        verify(userRepository).findById(999L);
	    }

	    @Test
	    @DisplayName("Should get user by username")
	    void shouldGetUserByUsername() {
	        // Given
	        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
	        when(userMapper.toDTO(testUser)).thenReturn(UserDto);

	        // When
	        UserDto result = userService.getUserByUsername("testuser");

	        // Then
	        assertThat(result).isNotNull();
	        assertThat(result.getUsername()).isEqualTo("testuser");
	        
	        verify(userRepository).findByUsername("testuser");
	    }

	    @Test
	    @DisplayName("Should create new user successfully")
	    void shouldCreateUserSuccessfully() {
	        // Given
	        UserCreateDto createDTO = UserCreateDto.builder()
	            .username("newuser")
	            .email("new@example.com")
	            .password("password123")
	            .firstName("New")
	            .lastName("User")
	            .roleIds(Set.of(1L))
	            .build();

	        when(userRepository.existsByUsername("newuser")).thenReturn(false);
	        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
	        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
	        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
	        when(userMapper.toEntity(createDTO)).thenReturn(testUser);
	        when(userRepository.save(any(User.class))).thenReturn(testUser);
	        when(userMapper.toDTO(testUser)).thenReturn(UserDto);

	        // When
	        UserDto result = userService.createUser(createDTO);

	        // Then
	        assertThat(result).isNotNull();
	        assertThat(result.getUsername()).isEqualTo("testuser");
	        
	        verify(userRepository).existsByUsername("newuser");
	        verify(userRepository).existsByEmail("new@example.com");
	        verify(passwordEncoder).encode("password123");
	        verify(userRepository).save(any(User.class));
	    }

	    @Test
	    @DisplayName("Should throw exception when creating user with existing username")
	    void shouldThrowExceptionWhenCreatingUserWithExistingUsername() {
	        // Given
	        UserCreateDto createDTO = UserCreateDto.builder()
	            .username("existinguser")
	            .email("new@example.com")
	            .password("password123")
	            .roleIds(Set.of(1L))
	            .build();

	        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

	        // When/Then
	        assertThatThrownBy(() -> userService.createUser(createDTO))
	            .isInstanceOf(IllegalArgumentException.class)
	            .hasMessage("Username déjà utilisé");
	        
	        verify(userRepository).existsByUsername("existinguser");
	        verify(userRepository, never()).save(any(User.class));
	    }

	    @Test
	    @DisplayName("Should update user successfully")
	    void shouldUpdateUserSuccessfully() {
	        // Given
	        UserUpdateDto updateDTO = UserUpdateDto.builder()
	            .email("updated@example.com")
	            .firstName("Updated")
	            .lastName("User")
	            .enabled(true)
	            .roleIds(Set.of(1L))
	            .build();

	        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
	        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
	        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
	        when(userRepository.save(testUser)).thenReturn(testUser);
	        when(userMapper.toDTO(testUser)).thenReturn(UserDto);

	        // When
	        UserDto result = userService.updateUser(1L, updateDTO);

	        // Then
	        assertThat(result).isNotNull();
	        verify(userRepository).findById(1L);
	        verify(userMapper).updateEntityFromDTO(updateDTO, testUser);
	        verify(userRepository).save(testUser);
	    }

	    @Test
	    @DisplayName("Should delete user successfully")
	    void shouldDeleteUserSuccessfully() {
	        // Given
	        when(userRepository.existsById(1L)).thenReturn(true);

	        // When
	        userService.deleteUser(1L);

	        // Then
	        verify(userRepository).existsById(1L);
	        verify(userRepository).deleteById(1L);
	    }

	    @Test
	    @DisplayName("Should throw exception when deleting non-existent user")
	    void shouldThrowExceptionWhenDeletingNonExistentUser() {
	        // Given
	        when(userRepository.existsById(999L)).thenReturn(false);

	        // When/Then
	        assertThatThrownBy(() -> userService.deleteUser(999L))
	            .isInstanceOf(ResourceNotFoundException.class)
	            .hasMessageContaining("User not found with id: 999");
	        
	        verify(userRepository).existsById(999L);
	        verify(userRepository, never()).deleteById(anyLong());
	    }

	    @Test
	    @DisplayName("Should change password successfully")
	    void shouldChangePasswordSuccessfully() {
	        // Given
	        PasswordChangeDto passwordDTO = PasswordChangeDto.builder()
	            .oldPassword("oldPassword")
	            .newPassword("newPassword")
	            .build();

	        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
	        when(passwordEncoder.matches("oldPassword", testUser.getPassword())).thenReturn(true);
	        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");

	        // When
	        userService.changePassword(1L, passwordDTO);

	        // Then
	        verify(userRepository).findById(1L);
	        verify(passwordEncoder).matches(eq("oldPassword"), anyString());
	        verify(passwordEncoder).encode("newPassword");
	        verify(userRepository).save(testUser);
	    }

	    @Test
	    @DisplayName("Should throw exception when old password is incorrect")
	    void shouldThrowExceptionWhenOldPasswordIncorrect() {
	        // Given
	        PasswordChangeDto passwordDTO = PasswordChangeDto.builder()
	            .oldPassword("wrongPassword")
	            .newPassword("newPassword")
	            .build();

	        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
	        when(passwordEncoder.matches("wrongPassword", testUser.getPassword())).thenReturn(false);

	        // When/Then
	        assertThatThrownBy(() -> userService.changePassword(1L, passwordDTO))
	            .isInstanceOf(IllegalArgumentException.class)
	            .hasMessage("Ancien mot de passe incorrect");
	        
	        verify(userRepository, never()).save(any(User.class));
	    }

}
