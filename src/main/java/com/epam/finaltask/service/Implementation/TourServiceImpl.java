package com.epam.finaltask.service.Implementation;

import java.math.BigDecimal;
import java.util.List;

import com.epam.finaltask.DTO.TourDTO;
import com.epam.finaltask.exception.BadRequestException;
import com.epam.finaltask.exception.NotFoundException;
import com.epam.finaltask.mapper.TourMapper;
import com.epam.finaltask.model.Tour;
import com.epam.finaltask.model.User;
import com.epam.finaltask.model.enums.HotelType;
import com.epam.finaltask.model.enums.TourType;
import com.epam.finaltask.model.enums.TransferType;
import com.epam.finaltask.model.enums.TourStatus;
import com.epam.finaltask.repository.TourRepository;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.service.TourService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
        log.info("Create tour attempt: {}", tourDTO);
        if (tourDTO == null) {
            log.error("Create tour failed: tourDTO is null");
            throw new RuntimeException("Tour data is required");
        }

        Tour tour = tourMapper.toTour(tourDTO);
        if (tour.getStatus() == null) {
            tour.setStatus(TourStatus.AVAILABLE);
        }

        Tour saved = tourRepository.save(tour);
        log.info("Tour created successfully: id={}, title={}", saved.getId(), saved.getTitle());
        return tourMapper.toTourDTO(saved);
    }

    @Override
    public TourDTO order(Long tourId, Long userId) {
        log.info("Order tour attempt: tourId={}, userId={}", tourId, userId);

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> {
                    log.error("Order failed: tour not found, tourId={}", tourId);
                    return new NotFoundException("Tour not found: " + tourId);
                });

        if (tour.getUser() != null) {
            log.warn("Order rejected: tour already ordered, tourId={}, currentUserId={}", tourId, tour.getUser().getId());
            throw new RuntimeException("Tour is already ordered by another user.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Order failed: user not found, userId={}", userId);
                    return new NotFoundException("User not found: " + userId);
                });

        tour.setUser(user);
        tour.setStatus(TourStatus.AVAILABLE);

        Tour saved = tourRepository.save(tour);
        log.info("Tour ordered successfully: tourId={}, userId={}", saved.getId(), user.getId());
        return tourMapper.toTourDTO(saved);
    }

    @Override
    public TourDTO update(TourDTO tourDTO) {
        log.info("Update tour attempt: {}", tourDTO);

        Tour existingTour = tourRepository.findById(tourDTO.getId())
                .orElseThrow(() -> {
                    log.error("Update failed: tour not found, tourId={}", tourDTO.getId());
                    return new NotFoundException("Tour not found: " + tourDTO.getId());
                });

        try {
            if (tourDTO.getTitle() != null) existingTour.setTitle(tourDTO.getTitle());
            if (tourDTO.getDescription() != null) existingTour.setDescription(tourDTO.getDescription());
            if (tourDTO.getPrice() != null) existingTour.setPrice(BigDecimal.valueOf(tourDTO.getPrice()));
            if (tourDTO.getTourType() != null) existingTour.setTourType(tourDTO.getTourType());
            if (tourDTO.getTransferType() != null) existingTour.setTransferType(tourDTO.getTransferType());
            if (tourDTO.getHotelType() != null) existingTour.setHotelType(tourDTO.getHotelType());
            if (tourDTO.getStatus() != null) existingTour.setStatus(tourDTO.getStatus());
            if (tourDTO.getArrivalDate() != null) existingTour.setArrivalDate(tourDTO.getArrivalDate());
            if (tourDTO.getEvictionDate() != null) existingTour.setEvictionDate(tourDTO.getEvictionDate());
            if (tourDTO.getIsHot() != null) existingTour.setHot(tourDTO.getIsHot());

            if (tourDTO.getUserId() != null) {
                User user = userRepository.findById(tourDTO.getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found: " + tourDTO.getUserId()));
                existingTour.setUser(user);
            }
        } catch (Exception e) {
            log.error("Update failed: exception during update, tourId={}", tourDTO.getId(), e);
            throw e;
        }

        Tour savedTour = tourRepository.save(existingTour);
        log.info("Tour updated successfully: tourId={}", savedTour.getId());
        return tourMapper.toTourDTO(savedTour);
    }

    @Override
    public void delete(Long tourId) {
        log.info("Delete tour attempt: tourId={}", tourId);

        if (!tourRepository.existsById(tourId)) {
            log.error("Delete failed: tour not found, tourId={}", tourId);
            throw new NotFoundException("Tour not found: " + tourId);
        }

        tourRepository.deleteById(tourId);
        log.info("Tour deleted successfully: tourId={}", tourId);
    }

    @Override
    public TourDTO changeHotStatus(Long tourId, TourDTO tourDTO) {
        log.info("Change hot status attempt: tourId={}", tourId);

        Tour existingTour = tourRepository.findById(tourId)
                .orElseThrow(() -> {
                    log.error("Change hot status failed: tour not found, tourId={}", tourId);
                    return new NotFoundException("Tour not found: " + tourId);
                });

        if (tourDTO != null && tourDTO.getIsHot() != null) {
            existingTour.setHot(tourDTO.getIsHot());
        } else {
            existingTour.setHot(!existingTour.isHot());
        }

        Tour savedTour = tourRepository.save(existingTour);
        log.info("Change hot status successful: tourId={}, isHot={}", savedTour.getId(), savedTour.isHot());
        return tourMapper.toTourDTO(savedTour);
    }

    @Override
    public TourDTO findById(Long id) {
        log.info("Find tour by id attempt: id={}", id);
        TourDTO dto = tourRepository.findById(id)
                .map(tourMapper::toTourDTO)
                .orElseThrow(() -> {
                    log.error("Find failed: tour not found, id={}", id);
                    return new NotFoundException("Tour with id: " + id + " not found");
                });
        log.info("Find tour successful: id={}", id);
        return dto;
    }

    @Override
    public List<TourDTO> findAllByUserId(Long userId) {
        log.info("Find all tours by userId: {}", userId);
        List<TourDTO> tours = tourRepository.findAllByUserId(userId).stream()
                .map(tourMapper::toTourDTO)
                .toList();
        log.info("Found {} tours for userId={}", tours.size(), userId);
        return tours;
    }

    @Override
    public List<TourDTO> findAllByTourType(TourType tourType) {
        if (tourType == null) {
            log.error("findAllByTourType failed: tourType is null");
            throw new RuntimeException("tourType is required");
        }
        log.info("Find all tours by tourType={}", tourType);
        List<TourDTO> tours = tourRepository.findAllByTourType(tourType).stream()
                .map(tourMapper::toTourDTO)
                .toList();
        log.info("Found {} tours for tourType={}", tours.size(), tourType);
        return tours;
    }

    @Override
    public List<TourDTO> findAllByTransferType(TransferType transferType) {
        if (transferType == null) {
            log.error("findAllByTransferType failed: transferType is null");
            throw new BadRequestException("transferType is required");
        }
        log.info("Find all tours by transferType={}", transferType);
        List<TourDTO> tours = tourRepository.findAllByTransferType(transferType).stream()
                .map(tourMapper::toTourDTO)
                .toList();
        log.info("Found {} tours for transferType={}", tours.size(), transferType);
        return tours;
    }

    @Override
    public List<TourDTO> findAllByPrice(Double price) {
        if (price == null) {
            log.error("findAllByPrice failed: price is null");
            throw new BadRequestException("price is required");
        }
        log.info("Find all tours by price={}", price);
        List<TourDTO> tours = tourRepository.findAllByPrice(BigDecimal.valueOf(price)).stream()
                .map(tourMapper::toTourDTO)
                .toList();
        log.info("Found {} tours for price={}", tours.size(), price);
        return tours;
    }

    @Override
    public List<TourDTO> findAllByHotelType(HotelType hotelType) {
        if (hotelType == null) {
            log.error("findAllByHotelType failed: hotelType is null");
            throw new BadRequestException("hotelType is required");
        }
        log.info("Find all tours by hotelType={}", hotelType);
        List<TourDTO> tours = tourRepository.findAllByHotelType(hotelType).stream()
                .map(tourMapper::toTourDTO)
                .toList();
        log.info("Found {} tours for hotelType={}", tours.size(), hotelType);
        return tours;
    }

    @Override
    public List<TourDTO> findAll() {
        log.info("Find all tours attempt");
        List<TourDTO> tours = tourRepository.findAll().stream()
                .map(tourMapper::toTourDTO)
                .toList();
        log.info("Found {} tours in total", tours.size());
        return tours;
    }

    @Override
    public List<TourDTO> findAllByStatus(TourStatus tourStatus) {
        log.info("Find all Available tours attempt");
        List<TourDTO> tours = tourRepository.findAllByStatus(tourStatus).stream()
                .map(tourMapper::toTourDTO)
                .toList();
        log.info("Found {} tours in total", tours.size());
        return tours;

    }
}
