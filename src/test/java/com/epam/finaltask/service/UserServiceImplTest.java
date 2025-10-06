package com.epam.finaltask.service;

import com.epam.finaltask.DTO.UserDTO;
import com.epam.finaltask.exception.BadRequestException;
import com.epam.finaltask.exception.NotFoundException;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.model.User;
import com.epam.finaltask.model.enums.Role;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.service.Implementation.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    // --- helpers ---
    private User buildUser(Long id, String username, String password, Role role, String email) {
        User u = new User();
        u.setId(id);
        u.setUsername(username);
        u.setPassword(password);
        u.setRole(role);
        u.setEmail(email);
        return u;
    }

    private UserDTO buildUserDTO(Long id, String username, String password, Role role, String email) {
        UserDTO dto = new UserDTO();
        dto.setId(id);
        dto.setUsername(username);
        dto.setPassword(password);
        dto.setRole(role);
        dto.setEmail(email);
        return dto;
    }

    // --- register ---
    @Test
    void register_success() {
        UserDTO dto = buildUserDTO(null, "user@example.com", "secret123", null, "user@example.com");

        when(userRepository.existsByUsername("user@example.com")).thenReturn(false);

        User toSave = buildUser(null, "user@example.com", "secret123", Role.USER, "user@example.com");
        when(userMapper.toUser(dto)).thenReturn(toSave);

        when(passwordEncoder.encode("secret123")).thenReturn("encoded-secret");

        User saved = buildUser(1L, "user@example.com", "encoded-secret", Role.USER, "user@example.com");
        when(userRepository.save(toSave)).thenReturn(saved);

        UserDTO expectedDto = buildUserDTO(1L, "user@example.com", null, Role.USER, "user@example.com");
        when(userMapper.toUserDTO(saved)).thenReturn(expectedDto);

        UserDTO result = userService.register(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("user@example.com", result.getUsername());
        verify(userRepository).existsByUsername("user@example.com");
        verify(passwordEncoder).encode("secret123");
        verify(userRepository).save(toSave);
        verify(userMapper).toUserDTO(saved);
    }

    @Test
    void register_nullDto_throwsBadRequest() {
        assertThrows(BadRequestException.class, () -> userService.register(null));
    }

    @Test
    void register_existingUsername_throwsIllegalArgument() {
        UserDTO dto = buildUserDTO(null, "user@example.com", "secret123", null, "user@example.com");
        when(userRepository.existsByUsername("user@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.register(dto));
    }

    @Test
    void register_usernameBlank_throwsBadRequest() {
        UserDTO dto = buildUserDTO(null, "   ", "pw", null, "a@a");
        assertThrows(BadRequestException.class, () -> userService.register(dto));
    }

    // --- updateUser ---
    @Test
    void updateUser_success_updatePassword() {
        String username = "john";
        UserDTO dto = buildUserDTO(null, username, "newpassword", null, "john@example.com");

        User existing = buildUser(2L, username, "oldEncoded", Role.USER, "john@example.com");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existing));

        when(passwordEncoder.encode("newpassword")).thenReturn("newEncoded");

        User saved = buildUser(2L, username, "newEncoded", Role.USER, "john@example.com");
        when(userRepository.save(existing)).thenReturn(saved);

        when(userMapper.toUserDTO(saved)).thenReturn(buildUserDTO(2L, username, null, Role.USER, "john@example.com"));

        UserDTO result = userService.updateUser(username, dto);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        verify(userRepository).findByUsername(username);
        verify(passwordEncoder).encode("newpassword");
        verify(userRepository).save(existing);
    }

    @Test
    void updateUser_noPassword_noChange() {
        String username = "nopw";
        UserDTO dto = buildUserDTO(null, username, null, null, "nopw@example.com");

        User existing = buildUser(33L, username, "unchanged", Role.USER, "nopw@example.com");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);
        when(userMapper.toUserDTO(existing)).thenReturn(buildUserDTO(33L, username, null, Role.USER, "nopw@example.com"));

        UserDTO result = userService.updateUser(username, dto);

        assertNotNull(result);
        assertEquals(33L, result.getId());
        assertEquals("unchanged", existing.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(existing);
    }

    @Test
    void updateUser_passwordTooShort_throwsIllegalArgument() {
        String username = "john";
        UserDTO dto = buildUserDTO(null, username, "123", null, "john@example.com");

        User existing = buildUser(2L, username, "oldEncoded", Role.USER, "john@example.com");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(username, dto));
    }

    @Test
    void updateUser_userNotFound_throwsNotFound() {
        String username = "missing";
        UserDTO dto = buildUserDTO(null, username, "newpass123", null, "m@example.com");
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(username, dto));
    }

    @Test
    void updateUser_nullArguments_throwsBadRequest() {
        assertThrows(BadRequestException.class, () -> userService.updateUser(null, null));
    }

    // --- getUserByUsername ---
    @Test
    void getUserByUsername_success() {
        String username = "anna";
        User user = buildUser(5L, username, "pwd", Role.USER, "a@example.com");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userMapper.toUserDTO(user)).thenReturn(buildUserDTO(5L, username, null, Role.USER, "a@example.com"));

        UserDTO result = userService.getUserByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

    @Test
    void getUserByUsername_nullUsername_throwsBadRequest() {
        assertThrows(BadRequestException.class, () -> userService.getUserByUsername(null));
    }

    @Test
    void getUserByUsername_blankUsername_throwsBadRequest() {
        assertThrows(BadRequestException.class, () -> userService.getUserByUsername("   "));
    }

    @Test
    void getUserByUsername_notFound_throwsNotFound() {
        when(userRepository.findByUsername("nope")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getUserByUsername("nope"));
    }

    // --- changeUserRole ---
    @Test
    void changeUserRole_success() {
        Long id = 10L;
        Role newRole = Role.ADMIN;
        User user = buildUser(id, "peter", "pwd", Role.USER, "p@example.com");
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        userService.changeUserRole(id, newRole);

        assertEquals(newRole, user.getRole());
        verify(userRepository).findById(id);
        verify(userRepository).save(user);
    }

    @Test
    void changeUserRole_nullArguments_throwsBadRequest() {
        assertThrows(BadRequestException.class, () -> userService.changeUserRole(null, null));
    }

    @Test
    void changeUserRole_userNotFound_throwsIllegalArgument() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.changeUserRole(99L, Role.ADMIN));
    }

    // --- getUserById ---
    @Test
    void getUserById_success() {
        Long id = 7L;
        User user = buildUser(id, "kate", "pwd", Role.USER, "k@example.com");
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toUserDTO(user)).thenReturn(buildUserDTO(id, "kate", null, Role.USER, "k@example.com"));

        UserDTO result = userService.getUserById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void getUserById_nullId_throwsBadRequest() {
        assertThrows(BadRequestException.class, () -> userService.getUserById(null));
    }

    @Test
    void getUserById_notFound_throwsNotFound() {
        when(userRepository.findById(55L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getUserById(55L));
    }

    // --- getUserByEmail (public non-interface method) ---
    @Test
    void getUserByEmail_success() {
        String email = "mail@domain.com";
        User user = buildUser(3L, "u3", "pwd", Role.USER, email);
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.toUserDTO(user)).thenReturn(buildUserDTO(3L, "u3", null, Role.USER, email));

        UserDTO result = userService.getUserByEmail(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void getUserByEmail_nullEmail_throwsBadRequest() {
        assertThrows(BadRequestException.class, () -> userService.getUserByEmail(null));
    }

    @Test
    void getUserByEmail_notFound_throwsNotFound() {
        when(userRepository.findUserByEmail("no@mail" )).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getUserByEmail("no@mail"));
    }

    // --- findAllUsers ---
    @Test
    void findAllUsers_returnsList() {
        User u1 = buildUser(1L, "a", "p", Role.USER, "a@a");
        User u2 = buildUser(2L, "b", "p", Role.ADMIN, "b@b");
        when(userRepository.findAll()).thenReturn(List.of(u1, u2));
        when(userMapper.toUserDTO(u1)).thenReturn(buildUserDTO(1L, "a", null, Role.USER, "a@a"));
        when(userMapper.toUserDTO(u2)).thenReturn(buildUserDTO(2L, "b", null, Role.ADMIN, "b@b"));

        var list = userService.findAllUsers();

        assertNotNull(list);
        assertEquals(2, list.size());
        verify(userRepository).findAll();
    }

    @Test
    void findAllUsers_empty_returnsEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());
        var list = userService.findAllUsers();
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }
}
