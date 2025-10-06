package com.epam.finaltask.service;

import com.epam.finaltask.exception.NotFoundException;
import com.epam.finaltask.model.User;
import com.epam.finaltask.model.enums.Role;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.service.Implementation.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_success_returnsUserDetails() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("john.doe@example.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);

        when(userRepository.findByUsername("john.doe@example.com")).thenReturn(Optional.of(user));

        // act
        UserDetails ud = customUserDetailsService.loadUserByUsername("john.doe@example.com");

        // assert
        assertNotNull(ud);
        assertEquals("john.doe@example.com", ud.getUsername());
        assertEquals("encodedPassword", ud.getPassword());
        // roles() in builder produces authority with prefix ROLE_
        assertTrue(ud.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));

        verify(userRepository).findByUsername("john.doe@example.com");
    }

    @Test
    void loadUserByUsername_userNotFound_throwsNotFoundException() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> customUserDetailsService.loadUserByUsername("missing"));
        assertTrue(ex.getMessage().contains("missing"));

        verify(userRepository).findByUsername("missing");
    }
}
