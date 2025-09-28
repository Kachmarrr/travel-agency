package com.epam.finaltask.service;

import java.util.List;

public interface BookingService {

    void book(Long tourId, Long userId);

    void cancelBooking(Long tourId);

}
