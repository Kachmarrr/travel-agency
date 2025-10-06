package com.epam.finaltask.service;

import com.epam.finaltask.exception.BadRequestException;
import com.epam.finaltask.exception.NotFoundException;
import com.epam.finaltask.model.Tour;
import com.epam.finaltask.model.User;
import com.epam.finaltask.model.enums.TourStatus;
import com.epam.finaltask.repository.TourRepository;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.service.Implementation.BookingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TourRepository tourRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    // --- helpers ---
    private User buildUser(Long id, BigDecimal balance) {
        User u = new User();
        u.setId(id);
        u.setBalance(balance);
        return u;
    }

    private Tour buildTour(Long id, BigDecimal price, TourStatus status, Long userId) {
        Tour t = new Tour();
        t.setId(id);
        t.setPrice(price);
        t.setStatus(status);
        if (userId != null) {
            User u = new User();
            u.setId(userId);
            t.setUser(u);
        }
        return t;
    }

    // --- book ---
    @Test
    void book_userNotFound_throwsNotFound() {
        when(userRepository.findUserById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.book(10L, 1L));
        verify(userRepository).findUserById(1L);
        verifyNoInteractions(tourRepository);
    }

    @Test
    void book_tourNotFound_throwsNotFound() {
        User user = buildUser(2L, BigDecimal.valueOf(1000));
        when(userRepository.findUserById(2L)).thenReturn(Optional.of(user));
        when(tourRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.book(99L, 2L));
        verify(tourRepository).findById(99L);
    }

    @Test
    void book_alreadyPaid_throwsBadRequest() {
        User user = buildUser(3L, BigDecimal.valueOf(1000));
        Tour tour = buildTour(5L, BigDecimal.valueOf(100), TourStatus.PAID, 7L);
        when(userRepository.findUserById(3L)).thenReturn(Optional.of(user));
        when(tourRepository.findById(5L)).thenReturn(Optional.of(tour));

        assertThrows(BadRequestException.class, () -> bookingService.book(5L, 3L));
        verify(tourRepository).findById(5L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void book_insufficientFunds_throwsBadRequest() {
        User user = buildUser(4L, BigDecimal.valueOf(50));
        Tour tour = buildTour(6L, BigDecimal.valueOf(100), TourStatus.AVAILABLE, null);
        when(userRepository.findUserById(4L)).thenReturn(Optional.of(user));
        when(tourRepository.findById(6L)).thenReturn(Optional.of(tour));

        assertThrows(BadRequestException.class, () -> bookingService.book(6L, 4L));
        // ensure no save performed
        verify(userRepository, never()).save(any());
        verify(tourRepository, never()).save(any());
    }

    @Test
    void book_success_debitsUserAndMarksTourPaid() {
        User user = buildUser(8L, BigDecimal.valueOf(500));
        Tour tour = buildTour(20L, BigDecimal.valueOf(120), TourStatus.AVAILABLE, null);
        Tour savedTour = buildTour(20L, BigDecimal.valueOf(120), TourStatus.PAID, 8L);

        when(userRepository.findUserById(8L)).thenReturn(Optional.of(user));
        when(tourRepository.findById(20L)).thenReturn(Optional.of(tour));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(tourRepository.save(any(Tour.class))).thenAnswer(i -> i.getArgument(0));

        bookingService.book(20L, 8L);

        // balance decreased
        assertEquals(BigDecimal.valueOf(380).setScale(2), user.getBalance().setScale(2));
        assertEquals(TourStatus.PAID, tour.getStatus());
        assertNotNull(tour.getUser());
        assertEquals(8L, tour.getUser().getId());

        verify(userRepository).save(user);
        verify(tourRepository).save(tour);
    }

    @Test
    void book_success_whenBalanceEqualsPrice() {
        User user = buildUser(9L, BigDecimal.valueOf(200));
        Tour tour = buildTour(21L, BigDecimal.valueOf(200), TourStatus.AVAILABLE, null);

        when(userRepository.findUserById(9L)).thenReturn(Optional.of(user));
        when(tourRepository.findById(21L)).thenReturn(Optional.of(tour));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(tourRepository.save(any(Tour.class))).thenAnswer(i -> i.getArgument(0));

        bookingService.book(21L, 9L);

        assertEquals(BigDecimal.ZERO.setScale(2), user.getBalance().setScale(2));
        assertEquals(TourStatus.PAID, tour.getStatus());
        verify(userRepository).save(user);
        verify(tourRepository).save(tour);
    }

    // --- cancelBooking ---
    @Test
    void cancelBooking_tourNotFound_throwsNotFound() {
        when(tourRepository.findById(123L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.cancelBooking(123L));
    }

    @Test
    void cancelBooking_alreadyAvailable_throwsBadRequest() {
        Tour tour = buildTour(130L, BigDecimal.valueOf(50), TourStatus.AVAILABLE, null);
        when(tourRepository.findById(130L)).thenReturn(Optional.of(tour));
        assertThrows(BadRequestException.class, () -> bookingService.cancelBooking(130L));
    }

    @Test
    void cancelBooking_noAssociatedUser_throwsBadRequest() {
        Tour tour = buildTour(140L, BigDecimal.valueOf(70), TourStatus.PAID, null);
        when(tourRepository.findById(140L)).thenReturn(Optional.of(tour));
        assertThrows(BadRequestException.class, () -> bookingService.cancelBooking(140L));
    }

    @Test
    void cancelBooking_success_refundsAndClearsBooking() {
        User user = buildUser(200L, BigDecimal.valueOf(300));
        Tour tour = buildTour(201L, BigDecimal.valueOf(120), TourStatus.PAID, 200L);
        // attach same user instance to tour so mutation affects user
        tour.setUser(user);

        when(tourRepository.findById(201L)).thenReturn(Optional.of(tour));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(tourRepository.save(any(Tour.class))).thenAnswer(i -> i.getArgument(0));

        bookingService.cancelBooking(201L);

        // balance refunded
        assertEquals(BigDecimal.valueOf(420).setScale(2), user.getBalance().setScale(2));
        assertEquals(TourStatus.AVAILABLE, tour.getStatus());
        assertNull(tour.getUser());

        verify(userRepository).save(user);
        verify(tourRepository).save(tour);
    }
}