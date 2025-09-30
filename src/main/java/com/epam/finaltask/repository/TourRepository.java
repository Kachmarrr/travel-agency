package com.epam.finaltask.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.epam.finaltask.model.Tour;
import com.epam.finaltask.model.enums.TourStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import com.epam.finaltask.model.enums.HotelType;
import com.epam.finaltask.model.enums.TourType;
import com.epam.finaltask.model.enums.TransferType;

public interface TourRepository extends JpaRepository<Tour, Long> {
    List<Tour> findAllByUserId(Long userId);
    List<Tour> findAllByTourType(TourType tourType);
    List<Tour> findAllByTransferType(TransferType transferType);
    List<Tour> findAllByPrice(BigDecimal price);
    List<Tour> findAllByHotelType(HotelType hotelType);
    List<Tour> findAllByStatus(TourStatus tourStatus);
}
