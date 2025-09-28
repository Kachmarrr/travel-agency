package com.epam.finaltask.mapper;

import com.epam.finaltask.DTO.TourDTO;
import com.epam.finaltask.model.Tour;

public interface TourMapper {
    Tour toTour(TourDTO tourDTO);
    TourDTO toTourDTO(Tour tour);
}
