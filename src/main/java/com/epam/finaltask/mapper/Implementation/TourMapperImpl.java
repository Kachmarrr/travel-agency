package com.epam.finaltask.mapper.Implementation;

import com.epam.finaltask.dto.TourDTO;
import com.epam.finaltask.mapper.TourMapper;
import com.epam.finaltask.model.Tour;
import com.epam.finaltask.model.enums.HotelType;
import com.epam.finaltask.model.enums.TourStatus;
import com.epam.finaltask.model.enums.TourType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TourMapperImpl implements TourMapper {

    @Override
    public Tour toTour(TourDTO dto) {
        if (dto == null) return null;

        Tour tour = Tour.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .price(BigDecimal.valueOf(dto.getPrice()))
                .tourType(TourType.valueOf(dto.getTourType()))
                .hotelType(HotelType.valueOf(dto.getHotelType()))
                .status(TourStatus.valueOf(dto.getStatus()))
                .arrivalDate(dto.getArrivalDate())
                .evictionDate(dto.getEvictionDate())
                .isHot(Boolean.TRUE.equals(dto.getIsHot()))
                .build();

        return tour;
    }

    @Override
    public TourDTO toTourDTO(Tour tour) {
        if (tour == null) return null;

        TourDTO tourDTO = TourDTO.builder()
                .title(tour.getTitle())
                .description(tour.getDescription())
                .price(Double.valueOf(String.valueOf(tour.getPrice())))
                .tourType(String.valueOf(tour.getTourType()))
                .hotelType(String.valueOf(tour.getHotelType()))
                .status(String.valueOf(tour.getStatus()))
                .arrivalDate(tour.getArrivalDate())
                .evictionDate(tour.getEvictionDate())
                .isHot(tour.isHot())
                .build();

        return tourDTO;
    }
}
