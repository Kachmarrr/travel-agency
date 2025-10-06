package com.epam.finaltask.controller;

import com.epam.finaltask.DTO.TourDTO;
import com.epam.finaltask.DTO.UserDTO;
import com.epam.finaltask.model.enums.Role;
import com.epam.finaltask.model.enums.TourStatus;
import com.epam.finaltask.service.TourService;
import com.epam.finaltask.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @Mock
    private TourService tourService;

    @Mock
    private UserService userService;

    @InjectMocks
    private HomeController homeController;

    @AfterEach
    void tearDown() {
        reset(tourService, userService);
    }

    @Test
    void index_addsAvailableToursAndReturnsIndexView() {
        Model model = new ExtendedModelMap();
        TourDTO t1 = new TourDTO(); t1.setId(1L);
        TourDTO t2 = new TourDTO(); t2.setId(2L);
        when(tourService.findAllByStatus(TourStatus.AVAILABLE)).thenReturn(List.of(t1, t2));

        String view = homeController.index(model);

        assertEquals("index", view);
        assertTrue(model.containsAttribute("tours"));
        Object attr = model.getAttribute("tours");
        assertNotNull(attr);
        assertEquals(2, ((List<?>) attr).size());
        verify(tourService).findAllByStatus(TourStatus.AVAILABLE);
    }

    @Test
    void dashboard_whenUserIsManager_showsAllTours() {

        Model model = new ExtendedModelMap();
        Principal principal = () -> "managerUser"; // is this wrong ?

        UserDTO user = new UserDTO();
        user.setId(5L);
        user.setUsername("managerUser");
        user.setRole(Role.MANAGER);

        TourDTO t1 = new TourDTO(); t1.setId(10L);
        when(userService.getUserByUsername("managerUser")).thenReturn(user);
        when(tourService.findAll()).thenReturn(List.of(t1));

        String view = homeController.dashboard(model, principal);

        assertEquals("dashboard", view);
        assertEquals(user, model.getAttribute("user"));
        assertTrue(model.containsAttribute("tours"));
        assertEquals(1, ((List<?>) model.getAttribute("tours")).size());

        verify(userService).getUserByUsername("managerUser");
        verify(tourService).findAll();
        verify(tourService, never()).findAllByStatus(TourStatus.AVAILABLE);
    }

    @Test
    void dashboard_whenUserIsNotManager_showsAvailableTours() {
        Model model = new ExtendedModelMap();
        Principal principal = () -> "simpleUser";


        UserDTO user = new UserDTO();
        user.setId(6L);
        user.setUsername("simpleUser");
        user.setRole(Role.USER);

        TourDTO t1 = new TourDTO(); t1.setId(11L);
        when(userService.getUserByUsername("simpleUser")).thenReturn(user);
        when(tourService.findAllByStatus(TourStatus.AVAILABLE)).thenReturn(List.of(t1));

        String view = homeController.dashboard(model, principal);

        assertEquals("dashboard", view);
        assertEquals(user, model.getAttribute("user"));
        assertTrue(model.containsAttribute("tours"));
        assertEquals(1, ((List<?>) model.getAttribute("tours")).size());

        verify(userService).getUserByUsername("simpleUser");
        verify(tourService).findAllByStatus(TourStatus.AVAILABLE);
        verify(tourService, never()).findAll();
    }

    @Test
    void profile_addsUserAndUserToursAndReturnsProfileView() {
        Model model = new ExtendedModelMap();
        Principal principal = () -> "anna";


        UserDTO user = new UserDTO();
        user.setId(77L);
        user.setUsername("anna");

        TourDTO t1 = new TourDTO(); t1.setId(21L);
        when(userService.getUserByUsername("anna")).thenReturn(user);
        when(tourService.findAllByUserId(77L)).thenReturn(List.of(t1));

        String view = homeController.profile(model, principal);

        assertEquals("user/profile", view);
        assertEquals(user, model.getAttribute("user"));
        assertTrue(model.containsAttribute("userTours"));
        assertEquals(1, ((List<?>) model.getAttribute("userTours")).size());

        verify(userService).getUserByUsername("anna");
        verify(tourService).findAllByUserId(77L);
    }
}
