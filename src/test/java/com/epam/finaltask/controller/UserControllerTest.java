package com.epam.finaltask.controller;

import com.epam.finaltask.DTO.UserDTO;
import com.epam.finaltask.exception.NotFoundException;
import com.epam.finaltask.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void findAllUsers_addsUsersToModelAndReturnsView() {
        Model model = new ExtendedModelMap();
        UserDTO u1 = new UserDTO(); u1.setId(1L);
        UserDTO u2 = new UserDTO(); u2.setId(2L);
        when(userService.findAllUsers()).thenReturn(List.of(u1, u2));

        String view = userController.findAllUsers(model);

        assertEquals("user/admin", view);
        assertTrue(model.containsAttribute("users"));
        assertEquals(2, ((List<?>) model.getAttribute("users")).size());
        verify(userService).findAllUsers();
    }

    @Test
    void makeManager_callsServiceAddsFlashAndRedirects() {
        RedirectAttributes ra = mock(RedirectAttributes.class);

        String view = userController.makeManager(10L, ra);

        assertEquals("redirect:/users", view);
        verify(userService).changeUserRole(10L, com.epam.finaltask.model.enums.Role.MANAGER);
        verify(ra).addFlashAttribute("success", "User role updated");
    }

    @Test
    void makeManager_whenServiceThrows_propagatesExceptionAndDoesNotAddFlash() {
        RedirectAttributes ra = mock(RedirectAttributes.class);
        doThrow(new IllegalArgumentException("no user")).when(userService).changeUserRole(99L, com.epam.finaltask.model.enums.Role.MANAGER);

        assertThrows(IllegalArgumentException.class, () -> userController.makeManager(99L, ra));

        verify(userService).changeUserRole(99L, com.epam.finaltask.model.enums.Role.MANAGER);
        verify(ra, never()).addFlashAttribute(anyString(), any());
    }

    @Test
    void removeManager_callsServiceAddsFlashAndRedirects() {
        RedirectAttributes ra = mock(RedirectAttributes.class);

        String view = userController.removeManager(20L, ra);

        assertEquals("redirect:/users", view);
        verify(userService).changeUserRole(20L, com.epam.finaltask.model.enums.Role.USER);
        verify(ra).addFlashAttribute("success", "User role updated");
    }

    @Test
    void removeManager_whenServiceThrows_propagatesExceptionAndDoesNotAddFlash() {
        RedirectAttributes ra = mock(RedirectAttributes.class);
        doThrow(new NotFoundException("not found")).when(userService).changeUserRole(88L, com.epam.finaltask.model.enums.Role.USER);

        assertThrows(NotFoundException.class, () -> userController.removeManager(88L, ra));

        verify(userService).changeUserRole(88L, com.epam.finaltask.model.enums.Role.USER);
        verify(ra, never()).addFlashAttribute(anyString(), any());
    }
}
