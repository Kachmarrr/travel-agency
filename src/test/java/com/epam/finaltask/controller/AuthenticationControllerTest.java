package com.epam.finaltask.controller;

import com.epam.finaltask.DTO.UserDTO;
import com.epam.finaltask.exception.NotFoundException;
import com.epam.finaltask.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationController controller;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void loginPage_returnsLoginView() {
        String view = controller.loginPage();
        assertEquals("auth/login", view);
    }

    @Test
    void getRegistrationForm_whenModelHasNoUser_addsUserAndReturnsView() {
        Model model = new ExtendedModelMap();
        String view = controller.getRegistrationForm(model);
        assertEquals("auth/register", view);
        assertTrue(model.containsAttribute("user"));
        Object u = model.getAttribute("user");
        assertNotNull(u);
        assertTrue(u instanceof UserDTO);
    }

    @Test
    void getRegistrationForm_whenModelAlreadyHasUser_doesNotReplace() {
        Model model = new ExtendedModelMap();
        UserDTO existing = new UserDTO();
        existing.setUsername("exists");
        model.addAttribute("user", existing);

        String view = controller.getRegistrationForm(model);
        assertEquals("auth/register", view);
        assertSame(existing, model.getAttribute("user"));
    }

    @Test
    void register_whenBindingErrors_returnsRegisterView() {
        UserDTO dto = new UserDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = controller.register(dto, bindingResult, redirectAttributes);
        assertEquals("auth/register", view);
        verifyNoInteractions(userService);
    }

    @Test
    void register_whenEmailExists_rejectsEmailAndReturnsRegister() {
        UserDTO dto = new UserDTO();
        dto.setEmail("a@a.com");
        dto.setUsername("u");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.getUserByEmail("a@a.com")).thenReturn(new UserDTO());

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = controller.register(dto, bindingResult, redirectAttributes);

        assertEquals("auth/register", view);
        verify(bindingResult).rejectValue("email", "error.user", "Email already in use");
        verify(userService, never()).register(any());
    }

    @Test
    void register_whenUsernameExists_rejectsUsernameAndReturnsRegister() {
        UserDTO dto = new UserDTO();
        dto.setEmail("free@a.com");
        dto.setUsername("taken");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        // email is free
        when(userService.getUserByEmail("free@a.com")).thenThrow(new NotFoundException("not found"));
        // username exists
        when(userService.getUserByUsername("taken")).thenReturn(new UserDTO());

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = controller.register(dto, bindingResult, redirectAttributes);

        assertEquals("auth/register", view);
        verify(bindingResult).rejectValue("username", "error.user", "Username already in use");
        verify(userService, never()).register(any());
    }

    @Test
    void register_success_registersUserAndRedirectsToLogin() {
        UserDTO dto = new UserDTO();
        dto.setEmail("free@a.com");
        dto.setUsername("freeuser");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.getUserByEmail("free@a.com")).thenThrow(new NotFoundException("not found"));
        when(userService.getUserByUsername("freeuser")).thenThrow(new NotFoundException("not found"));

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = controller.register(dto, bindingResult, redirectAttributes);

        assertEquals("redirect:/auth/login", view);
        verify(userService).register(dto);
        verify(redirectAttributes).addFlashAttribute("success", "Registration successful. Please log in.");
    }

    @Test
    void logout_whenNoAuth_returnsRedirectRoot() {
        // ensure no auth
        SecurityContextHolder.clearContext();

        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse resp = new MockHttpServletResponse();

        String view = controller.logout(req, resp);
        assertEquals("redirect:/", view);
    }

    @Test
    void logout_whenAuthPresent_clearsContextAndReturnsRedirect() {
        Authentication auth = new TestingAuthenticationToken("user", "password", "ROLE_USER");
        SecurityContextImpl ctx = new SecurityContextImpl();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);

        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse resp = new MockHttpServletResponse();

        String view = controller.logout(req, resp);
        assertEquals("redirect:/", view);
        // logout handler should clear the security context
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
