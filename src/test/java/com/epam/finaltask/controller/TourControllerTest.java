package com.epam.finaltask.controller;

import com.epam.finaltask.DTO.TourDTO;
import com.epam.finaltask.DTO.UserDTO;
import com.epam.finaltask.exception.NotFoundException;
import com.epam.finaltask.service.TourService;
import com.epam.finaltask.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourControllerTest {

    @Mock
    private TourService tourService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TourController controller;

    @Test
    void detail_whenTourHasNoUser_addsTourAndNullUser() {
        Model model = new ExtendedModelMap();
        TourDTO t = new TourDTO();
        t.setId(5L);
        t.setUserId(null);

        when(tourService.findById(5L)).thenReturn(t);

        String view = controller.detail(5L, model);

        assertEquals("tours/detail", view);
        assertSame(t, model.getAttribute("tour"));
        assertNull(model.getAttribute("user"));
        verify(tourService).findById(5L);
        verifyNoInteractions(userService);
    }

    @Test
    void detail_whenTourHasUser_fetchesUserAndAddsBoth() {
        Model model = new ExtendedModelMap();
        TourDTO t = new TourDTO();
        t.setId(6L);
        t.setUserId(11L);

        UserDTO user = new UserDTO();
        user.setId(11L);
        user.setUsername("owner");

        when(tourService.findById(6L)).thenReturn(t);
        when(userService.getUserById(11L)).thenReturn(user);

        String view = controller.detail(6L, model);

        assertEquals("tours/detail", view);
        assertSame(t, model.getAttribute("tour"));
        assertSame(user, model.getAttribute("user"));
        verify(tourService).findById(6L);
        verify(userService).getUserById(11L);
    }

    @Test
    void detail_whenTourNotFound_propagatesNotFoundException() {
        Model model = new ExtendedModelMap();
        when(tourService.findById(99L)).thenThrow(new NotFoundException("not found"));
        assertThrows(NotFoundException.class, () -> controller.detail(99L, model));
        verify(tourService).findById(99L);
    }

    @Test
    void createForm_addsEmptyDtoAndReturnsView() {
        Model model = new ExtendedModelMap();
        String view = controller.createForm(model);
        assertEquals("tours/create", view);
        assertTrue(model.containsAttribute("tourDTO"));
        assertTrue(model.getAttribute("tourDTO") instanceof TourDTO);
    }

    @Test
    void create_callsServiceAndRedirects() {
        TourDTO dto = new TourDTO();
        String view = controller.create(dto);
        assertEquals("redirect:/dashboard", view);
        verify(tourService).create(dto);
    }

    @Test
    void editForm_loadsTourAndReturnsView() {
        Model model = new ExtendedModelMap();
        TourDTO dto = new TourDTO(); dto.setId(7L);
        when(tourService.findById(7L)).thenReturn(dto);

        String view = controller.editForm(7L, model);

        assertEquals("tours/edit", view);
        assertSame(dto, model.getAttribute("tour"));
        verify(tourService).findById(7L);
    }

    @Test
    void editForm_whenNotFound_propagates() {
        Model model = new ExtendedModelMap();
        when(tourService.findById(50L)).thenThrow(new NotFoundException("no"));
        assertThrows(NotFoundException.class, () -> controller.editForm(50L, model));
        verify(tourService).findById(50L);
    }

    @Test
    void update_whenBindingHasErrors_returnsEditView() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        TourDTO dto = new TourDTO();
        String view = controller.update(12L, dto, bindingResult);

        assertEquals("tours/edit", view);
        // service should not be called
        verifyNoInteractions(tourService);
    }

    @Test
    void update_success_setsIdCallsServiceAndRedirects() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        TourDTO dto = new TourDTO();
        // id initially null
        String view = controller.update(13L, dto, bindingResult);

        assertEquals("redirect:/dashboard", view);

        ArgumentCaptor<TourDTO> captor = ArgumentCaptor.forClass(TourDTO.class);
        verify(tourService).update(captor.capture());
        TourDTO passed = captor.getValue();
        assertEquals(13L, passed.getId());
    }

    @Test
    void delete_callsServiceAndRedirects() {
        String view = controller.delete(33L);
        assertEquals("redirect:/dashboard", view);
        verify(tourService).delete(33L);
    }

    @Test
    void delete_whenServiceThrowsNotFound_propagates() {
        doThrow(new NotFoundException("nope")).when(tourService).delete(77L);
        assertThrows(NotFoundException.class, () -> controller.delete(77L));
        verify(tourService).delete(77L);
    }
}
