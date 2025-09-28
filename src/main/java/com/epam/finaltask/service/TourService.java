package com.epam.finaltask.service;

import java.util.List;

import com.epam.finaltask.DTO.TourDTO;
import com.epam.finaltask.model.enums.HotelType;
import com.epam.finaltask.model.enums.TourType;
import com.epam.finaltask.model.enums.TransferType;

public interface TourService {

    TourDTO create(TourDTO tourDTO);
    TourDTO order(Long tourId, Long userId);
    TourDTO update(TourDTO tourDTO);
    void delete(Long tourId);

    TourDTO findById(Long id);
    TourDTO changeHotStatus(Long tourId, TourDTO tourDTO);
    List<TourDTO> findAllByUserId(Long userId);


    List<TourDTO> findAllByTourType(TourType tourType);
    List<TourDTO>findAllByTransferType(TransferType transferType);
    List<TourDTO> findAllByPrice(Double price);
    List<TourDTO> findAllByHotelType(HotelType hotelType);

    List<TourDTO> findAll();
}
