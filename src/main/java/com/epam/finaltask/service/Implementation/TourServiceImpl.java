package com.epam.finaltask.service.Implementation;

import java.math.BigDecimal;
import java.util.List;

import com.epam.finaltask.exception.BadRequestException;
import com.epam.finaltask.exception.NotFoundException;
import com.epam.finaltask.model.Tour;
import com.epam.finaltask.model.User;
import com.epam.finaltask.model.enums.HotelType;
import com.epam.finaltask.model.enums.TourType;
import com.epam.finaltask.service.TourService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.finaltask.dto.TourDTO;
import com.epam.finaltask.mapper.TourMapper;
import com.epam.finaltask.model.enums.TransferType;
import com.epam.finaltask.model.enums.TourStatus;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.TourRepository;

@Service
@Transactional
public class TourServiceImpl implements TourService {

    private final TourRepository tourRepository;
    private final UserRepository userRepository;
    private final TourMapper tourMapper;

    public TourServiceImpl(TourRepository tourRepository, UserRepository userRepository, TourMapper tourMapper) {
        this.tourRepository = tourRepository;
        this.userRepository = userRepository;
        this.tourMapper = tourMapper;
    }

    @Override
    public TourDTO create(TourDTO tourDTO) {
        if (tourDTO == null) throw new RuntimeException("Tour data is required");

        Tour tour = tourMapper.toTour(tourDTO);

        if (tour.getStatus() == null) {
            tour.setStatus(TourStatus.REGISTERED);
        }

        Tour saved = tourRepository.save(tour);
        return tourMapper.toTourDTO(saved);
    }

    @Override
    public TourDTO order(Long tourId, Long userId) {

        Tour tour = tourRepository
                .findById(tourId)
                .orElseThrow(() -> new NotFoundException("Tour not found: " + tourId));

        if (tour.getUser() != null) {
            throw new RuntimeException("Tour is already ordered by another user.");
        }

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        tour.setUser(user);
        tour.setStatus(TourStatus.REGISTERED);

        Tour saved = tourRepository.save(tour);
        return tourMapper.toTourDTO(saved);
    }

    @Override
    public TourDTO update(Long tourId, TourDTO tourDTO) {

        Tour existingTour = tourRepository.
                findById(tourId)
                .orElseThrow(() -> new NotFoundException("Tour not found: " + tourId));

        if (tourDTO.getTitle() != null) existingTour.setTitle(tourDTO.getTitle());
        if (tourDTO.getDescription() != null) existingTour.setDescription(tourDTO.getDescription());
        if (tourDTO.getPrice() != null) existingTour.setPrice(BigDecimal.valueOf(tourDTO.getPrice()));
        if (tourDTO.getTourType() != null) {
            try {
                existingTour.setTourType(TourType.valueOf(tourDTO.getTourType()));
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (tourDTO.getTransferType() != null) {
            try {
                existingTour.setTransferType(TransferType.valueOf(tourDTO.getTransferType()));
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (tourDTO.getHotelType() != null) {
            try {
                existingTour.setHotelType(HotelType.valueOf(tourDTO.getHotelType()));
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (tourDTO.getStatus() != null) {
            try {
                existingTour.setStatus(TourStatus.valueOf(tourDTO.getStatus()));
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (tourDTO.getArrivalDate() != null) existingTour.setArrivalDate(tourDTO.getArrivalDate());
        if (tourDTO.getEvictionDate() != null) existingTour.setEvictionDate(tourDTO.getEvictionDate());
        if (tourDTO.getIsHot() != null) existingTour.setHot(tourDTO.getIsHot());

        if (tourDTO.getUserId() != null) {

            var user = userRepository
                    .findById(tourDTO.getId())
                    .orElseThrow(() -> new RuntimeException("User not found: " + tourDTO
                            .getUserId()));

            existingTour.setUser(user);
        }

        Tour savedTour = tourRepository.save(existingTour);
        return tourMapper.toTourDTO(savedTour);
    }

    @Override
    public void delete(Long tourId) {

        if (!tourRepository.existsById(tourId)) {
            throw new NotFoundException("Tour not found: " + tourId);
        }
        tourRepository.deleteById(tourId);
    }

    @Override
    public TourDTO changeHotStatus(Long tourId, TourDTO tourDTO) {

        Tour existingTour = tourRepository
                .findById(tourId)
                .orElseThrow(() -> new NotFoundException("Tour not found: " + tourId));

        //   if DTO isHot — set it; otherwise toggle
        if (tourDTO != null && tourDTO.getIsHot() != null) {
            existingTour.setHot(tourDTO.getIsHot());
        } else {
            existingTour.setHot(!existingTour.isHot());
        }

        Tour savedTour = tourRepository.save(existingTour);
        return tourMapper.toTourDTO(savedTour);
    }

    @Override
    public List<TourDTO> findAllByUserId(Long userId) {

        return tourRepository.findAllByUserId(userId).stream()
                .map(tourMapper::toTourDTO)
                .toList();

    }

    @Override
    public List<TourDTO> findAllByTourType(TourType tourType) {
        if (tourType == null) throw new RuntimeException("tourType is required");
        return tourRepository
                .findAllByTourType(tourType).stream()
                .map(tourMapper::toTourDTO)
                .toList();
    }

    @Override
    public List<TourDTO> findAllByTransferType(TransferType transferType) {

        if (transferType == null) {
            throw new RuntimeException("transferType is required");
        }

        return tourRepository
                .findAllByTransferType(transferType).stream()
                .map(tourMapper::toTourDTO)
                .toList();
    }

    @Override
    public List<TourDTO> findAllByPrice(Double price) {
        if (price == null) {
            throw new BadRequestException("price is required");
        }
        return tourRepository
                .findAllByPrice(BigDecimal.valueOf(price)).stream()
                .map(tourMapper::toTourDTO)
                .toList();
    }

    @Override
    public List<TourDTO> findAllByHotelType(HotelType hotelType) {
        if (hotelType == null) throw new BadRequestException("hotelType is required");
        return tourRepository
                .findAllByHotelType(hotelType).stream()
                .map(tourMapper::toTourDTO)
                .toList();
    }

    @Override
    public List<TourDTO> findAll() {
        return tourRepository
                .findAll().stream()
                .map(tourMapper::toTourDTO)
                .toList();
    }
}
