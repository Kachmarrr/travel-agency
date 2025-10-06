package com.epam.finaltask.service.Implementation;

import com.epam.finaltask.exception.BadRequestException;
import com.epam.finaltask.exception.NotFoundException;
import com.epam.finaltask.model.Tour;
import com.epam.finaltask.model.User;
import com.epam.finaltask.model.enums.TourStatus;
import com.epam.finaltask.repository.TourRepository;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final TourRepository tourRepository;

    public BookingServiceImpl(UserRepository userRepository, TourRepository tourRepository) {
        this.userRepository = userRepository;
        this.tourRepository = tourRepository;
    }

    @Override
    public void book(Long tourId, Long userId) {

        log.info("Booking attempt: tourId={}, userId={}", tourId, userId);

        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> {
                    log.error("Booking failed: user not found, userId={}", userId);
                    return new NotFoundException("User with id '" + userId + "' not found");
                });

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> {
                    log.error("Booking failed: tour not found, tourId={}", tourId);
                    return new NotFoundException("Tour not found: " + tourId);
                });

        if (tour.getStatus() == TourStatus.PAID) {
            log.warn("Booking rejected: tour already paid, tourId={}, userId={}", tourId, userId);
            throw new BadRequestException("This tour is already paid from another user");
        }

        BigDecimal price = tour.getPrice();
        if (user.getBalance().compareTo(price) < 0) {
            log.warn("Booking rejected: insufficient funds, userId={}, balance={}, required={}",
                    userId, user.getBalance(), price);
            throw new BadRequestException("You don't have enough money to book this tour!");
        }

        user.setBalance(user.getBalance().subtract(price));
        tour.setStatus(TourStatus.PAID);
        tour.setUser(user);

        userRepository.save(user);
        tourRepository.save(tour);

        log.info("Booking successful: tourId={}, userId={}, newBalance={}",
                tourId, userId, user.getBalance());
    }

    @Override
    public void cancelBooking(Long tourId) {
        log.info("Cancel booking attempt: tourId={}", tourId);

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> {
                    log.error("Cancel failed: tour not found, tourId={}", tourId);
                    return new NotFoundException("Tour not found: " + tourId);
                });

        if (tour.getStatus() == TourStatus.AVAILABLE) {
            log.warn("Cancel rejected: tour already available, tourId={}", tourId);
            throw new BadRequestException("This tour is already available, you can find it in the list of tours");
        }

        User user = tour.getUser();
        if (user == null) {
            log.error("Cancel failed: tour has no associated user, tourId={}", tourId);
            throw new BadRequestException("This tour has no associated user, cannot cancel booking");
        }

        BigDecimal price = tour.getPrice();
        user.setBalance(user.getBalance().add(price));

        tour.setStatus(TourStatus.AVAILABLE);
        tour.setUser(null);

        userRepository.save(user);
        tourRepository.save(tour);

        log.info("Cancel successful: tourId={}, refundedToUserId={}, newBalance={}",
                tourId, user.getId(), user.getBalance());
    }
}
