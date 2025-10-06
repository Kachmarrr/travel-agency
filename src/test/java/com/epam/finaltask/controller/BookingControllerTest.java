package com.epam.finaltask.controller;

import com.epam.finaltask.DTO.UserDTO;
import com.epam.finaltask.exception.NotFoundException;
import com.epam.finaltask.service.BookingService;
import com.epam.finaltask.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private UserService userService;

    @InjectMocks
    private BookingController bookingController;

    @Test
    void createBooking_success_callsBookingServiceAndRedirects() {

        Principal principal = () -> "user";

        UserDTO userDTO = new UserDTO();
        userDTO.setId(7L);
        when(userService.getUserByUsername("user")).thenReturn(userDTO);

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = bookingController.createBooking(10L, principal, redirectAttributes);

        assertEquals("redirect:/profile", view);
        verify(userService).getUserByUsername("user");
        verify(bookingService).book(10L, 7L);
        verify(redirectAttributes).addFlashAttribute("success", "Тур успішно заброньовано!");
    }

    @Test
    void createBooking_userNotFound_throwsNotFoundException() {
        Principal principal = () -> "none";

        when(userService.getUserByUsername("none")).thenThrow(new NotFoundException("not found"));

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        assertThrows(NotFoundException.class, () -> bookingController.createBooking(5L, principal, redirectAttributes));

        verify(userService).getUserByUsername("none");
        verifyNoInteractions(bookingService);
    }

    @Test
    void cancelBooking_success_callsServiceAndRedirects() {
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = bookingController.cancelBooking(15L, redirectAttributes);

        assertEquals("redirect:/profile", view);
        verify(bookingService).cancelBooking(15L);
        verify(redirectAttributes).addFlashAttribute("success", "Бронювання скасовано!");
    }

    @Test
    void cancelBooking_serviceThrowsNotFound_propagatesException() {
        doThrow(new NotFoundException("no tour")).when(bookingService).cancelBooking(99L);

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        assertThrows(NotFoundException.class, () -> bookingController.cancelBooking(99L, redirectAttributes));

        verify(bookingService).cancelBooking(99L);
        verifyNoInteractions(redirectAttributes);
    }
}
