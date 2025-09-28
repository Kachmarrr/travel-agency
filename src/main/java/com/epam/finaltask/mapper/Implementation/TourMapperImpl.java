package com.epam.finaltask.mapper.Implementation;

import com.epam.finaltask.DTO.TourDTO;
import com.epam.finaltask.mapper.TourMapper;
import com.epam.finaltask.model.Tour;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TourMapperImpl implements TourMapper {

    @Override
    public Tour toTour(TourDTO dto) {
        if (dto == null) return null;

        Tour tour = Tour.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .price(BigDecimal.valueOf(dto.getPrice()))
                .transferType(dto.getTransferType())
                .tourType(dto.getTourType())
                .hotelType(dto.getHotelType())
                .status(dto.getStatus())
                .arrivalDate(dto.getArrivalDate())
                .evictionDate(dto.getEvictionDate())
                .isHot(Boolean.TRUE.equals(dto.getIsHot()))
                .build();

        return tour;
    }

    @Override
    public TourDTO toTourDTO(Tour tour) {
        if (tour == null) return null;

        Long userId = (tour.getUser() != null) ? tour.getUser().getId() : null;

        return TourDTO.builder()
                .id(tour.getId())
                .title(tour.getTitle())
                .description(tour.getDescription())
                .price(tour.getPrice() != null ? tour.getPrice().doubleValue() : null)
                .transferType(tour.getTransferType())
                .tourType(tour.getTourType())
                .hotelType(tour.getHotelType())
                .status(tour.getStatus())
                .arrivalDate(tour.getArrivalDate())
                .evictionDate(tour.getEvictionDate())
                .isHot(Boolean.TRUE.equals(tour.isHot()))
                .userId(userId)
                .build();
    }
}
